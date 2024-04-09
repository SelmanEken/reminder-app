package com.example.hatirlatici3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {
    private MediaPlayer player;
    private Button stopAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activity oluşturulduğunda çağrılan metod
        Log.e("aaaa", "aaaaaa");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm); // activity_alarm.xml adındaki layout dosyasını kullanıyoruz
        IntentFilter intentFilter = new IntentFilter("STOP_ALARM_ACTION");
        registerReceiver(stopAlarmReceiver, intentFilter);
        player = MediaPlayer.create(this, R.raw.alarm_sound3);
        player.setLooping(true);
        player.start();

        stopAlarmButton = findViewById(R.id.stopAlarmButton);
        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Activity sonlandırıldığında çağrılan metod
        super.onDestroy();
        unregisterReceiver(stopAlarmReceiver);
    }

    private void stopAlarm() {
        // Alarmı durdurma işlemi gerçekleştirilir
        if (player != null && player.isPlaying()) {
            player.release();  // MediaPlayer'ı serbest bırak
            player = null;     // mediaPlayer'ı null'a ata
        }
        finish();
    }

    // STOP_ALARM_ACTION intenti alındığında çağrılan BroadcastReceiver
    private final BroadcastReceiver stopAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("STOP_ALARM_ACTION".equals(intent.getAction())) {
                stopAlarm();
            }
        }
    };
}
