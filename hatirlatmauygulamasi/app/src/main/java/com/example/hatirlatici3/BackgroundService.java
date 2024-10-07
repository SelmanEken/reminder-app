package com.example.hatirlatici3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service {
    @Override
    // Bu servis, onStartCommand metodu ile başlatıldığında, START_STICKY flag'i sayesinde işlem sonlandığında tekrar başlamasını sağlar.
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Servis bir bağlayıcı döndürmez, çünkü bu servis bağlı bir bileşenle etkileşimde bulunmaz.
        return null;
    }
}
