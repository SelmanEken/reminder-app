package com.example.hatirlatici3;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.hatirlatici3.helper.DatabaseHelper;
import com.example.hatirlatici3.helper.MySensorEventListener;
import com.example.hatirlatici3.model.Reminder;

public class AlarmReceiver extends BroadcastReceiver {
    Context context;
    private MySensorEventListener sensorEventListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        // alarm tetiklenme durumu
        this.context = context;
        Log.e("AlarmReceiver", "Alarm çaldı!");
        long reminderId = intent.getLongExtra("reminderId", -1);
        long delayInMillis = intent.getLongExtra("delay", 100);
        if (reminderId != -1) {
            Log.e("TAG", "reminderId:" + reminderId);
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            Reminder reminder = dbHelper.getReminder(reminderId);

            if (reminder != null) {
                sendNotification(context, reminder, delayInMillis);
            } else {
                Log.e("AlarmReceiver", "Hatırlatıcı bulunamadı!");
            }
        } else {
            Log.e("AlarmReceiver", "Geçersiz hatırlatıcı kimliği!");
        }

        // Sensor olaylarını dinlemek için
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorEventListener = new MySensorEventListener(this, context);
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Bildirim gönderme işlemi
    private void sendNotification(Context context, Reminder reminder, long delayInMillis) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Oreo ve üzeri sürümler için bildirim kanalı oluştur
            String channelId = "my_channel_id";
            CharSequence channelName = "My Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Bildirim tıklandığında açılacak aktivite için intent oluştur
        Intent intent = new Intent(context, MyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Bildirim oluştur
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_id")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(reminder.getTitle())
                .setContentText(reminder.getDescription())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        int notificationId = (int) reminder.getId();
        notificationManager.notify(notificationId, builder.build());

        // Belirli bir süre sonra AlarmActivity'yi aç
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openAlarmActivity(context);
            }
        }, delayInMillis);
    }

    // AlarmActivity'yi açma işlemi
    public void openAlarmActivity(Context context) {
        Intent myIntent = new Intent(context, MyActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

    // Alarmı durdurma işlemi
    public void stopAlarm(Context context) {
        Intent stopAlarmIntent = new Intent("STOP_ALARM_ACTION");
        context.sendBroadcast(stopAlarmIntent);
    }
}
