package com.example.shoppinglistapp.presentation.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.app.Service;

import androidx.core.app.NotificationCompat;

import com.example.shoppinglistapp.R;
import com.example.shoppinglistapp.presentation.ui.main.MainActivity;

public class ForegroundService extends Service {

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification channel (needed for Android 8.0 and above)
        String channelId = "default_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for your MainActivity
        Intent notificationIntent = new Intent(this, MainActivity.class);

        // Update PendingIntent to use FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_large);
        // Build the notification with the PendingIntent
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("CoupleCart is running")
                .setContentText("Tap to return to the app.")
                .setSmallIcon(R.drawable.ic_logo_small)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)  // Set the PendingIntent
                .setAutoCancel(true)  // Close the notification when clicked
                .build();

        // Start the service in the foreground, specifying the service type (for Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(startId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(startId, notification);
        }

        return START_STICKY;  // Keep the service running
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
