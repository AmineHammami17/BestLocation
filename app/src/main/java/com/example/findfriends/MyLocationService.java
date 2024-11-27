package com.example.findfriends;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyLocationService extends Service {

    private static final String CHANNEL_ID = "FindFriendsServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String numero = intent.getStringExtra("phone");
            if (numero != null) {
                startForegroundServiceWithNotification();
                FusedLocationProviderClient mClient = LocationServices.getFusedLocationProviderClient(this);
                mClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        sendSmsWithLocation(numero, longitude, latitude);
                    }
                    // Arrêter le service après l'envoi du SMS
                    stopSelf();
                });
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * Envoie un SMS avec les coordonnées GPS
     */
    private void sendSmsWithLocation(String numero, double longitude, double latitude) {
        try {
            SmsManager manager = SmsManager.getDefault();
            String message = "FindFriends : Ma position est #" + longitude + "#" + latitude;
            manager.sendTextMessage(numero, null, message, null, null);
            Log.d("MyLocationService", "SMS envoyé à " + numero);
        } catch (Exception e) {
            Log.e("MyLocationService", "Erreur lors de l'envoi du SMS", e);
        }
    }

    /**
     * Démarre le service au premier plan avec une notification
     */
    @SuppressLint("ForegroundServiceType")
    private void startForegroundServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Find Friends Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service de localisation")
                .setContentText("Envoi de votre position en cours...")
                .setSmallIcon(android.R.drawable.ic_dialog_map)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyLocationService", "Service détruit");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
