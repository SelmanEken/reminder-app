package com.example.hatirlatici3.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.example.hatirlatici3.AlarmReceiver;

public class MySensorEventListener implements SensorEventListener {
    // Sallama hassasiyeti
    private static final int SHAKE_THRESHOLD = 20;

    // Sallama sonrası bekleme süresi
    private static final int SHAKE_TIMEOUT = 1000;

    // Son sallama zamanı
    private long lastShakeTime = 0;

    private final AlarmReceiver alarmReceiver;

    Context context;

    public MySensorEventListener(AlarmReceiver alarmReceiver, Context context) {
        this.alarmReceiver = alarmReceiver;
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();

        // Son sallamadan belirlenen bekleme süresi kadar zaman geçtiyse devam et
        if ((currentTime - lastShakeTime) > SHAKE_TIMEOUT) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Cihazın ivmesi
            double acceleration = Math.sqrt(x * x + y * y + z * z);

            // Belirlenen sallama hassasiyetinden büyükse
            if (acceleration > SHAKE_THRESHOLD) {
                // Sallama algılandı, alarmı durdur
                alarmReceiver.stopAlarm(context);

                // Son sallama zamanını güncelle
                lastShakeTime = currentTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
