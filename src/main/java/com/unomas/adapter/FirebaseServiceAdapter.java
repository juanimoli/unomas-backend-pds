package com.unomas.adapter;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter para el servicio de Firebase Cloud Messaging
 * Patrón Adapter: Adapta Firebase Messaging a NotificacionServiceAdapter
 */
@Component
public class FirebaseServiceAdapter implements NotificacionServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseServiceAdapter.class);
    
    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;
    
    @Override
    public void enviarNotificacion(String token, String titulo, String mensaje) {
        if (!isDisponible()) {
            return;
        }
        
        try {
            String notificationId = String.valueOf(System.currentTimeMillis());
            
            Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(titulo)
                    .setBody(mensaje)
                    .build())
                .putData("notificationId", notificationId)
                .putData("timestamp", String.valueOf(System.currentTimeMillis()))
                .putData("type", "partido_update")
                .build();
            
            FirebaseMessaging.getInstance().send(message);
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación push: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean isDisponible() {
        return firebaseEnabled;
    }
}
