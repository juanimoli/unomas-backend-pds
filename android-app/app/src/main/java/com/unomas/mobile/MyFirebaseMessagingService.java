package com.unomas.mobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "unomas_notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            
            // Extraer el ID único de notificación de los datos
            int notificationId;
            if (remoteMessage.getData().containsKey("notificationId")) {
                try {
                    String notifIdStr = remoteMessage.getData().get("notificationId");
                    long timestampLong = Long.parseLong(notifIdStr);
                    notificationId = (int) (timestampLong & 0x7FFFFFFF);
                } catch (Exception e) {
                    notificationId = (int) (System.currentTimeMillis() & 0x7FFFFFFF);
                }
            } else {
                notificationId = (int) (System.currentTimeMillis() & 0x7FFFFFFF);
            }
            
            mostrarNotificacion(title, body, notificationId);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    private void mostrarNotificacion(String title, String message, int notificationId) {
        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notificaciones Uno Más",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de partidos deportivos");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }
}
