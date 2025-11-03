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
            logger.warn("Firebase no está habilitado. Simulando envío push");
            simularEnvio(token, titulo, mensaje);
            return;
        }
        
        try {
            Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(titulo)
                    .setBody(mensaje)
                    .build())
                .build();
            
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Notificación push enviada exitosamente. Response: {}", response);
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación push: {}", e.getMessage());
            // En caso de error, simular el envío
            simularEnvio(token, titulo, mensaje);
        }
    }
    
    @Override
    public boolean isDisponible() {
        return firebaseEnabled;
    }
    
    /**
     * Simula el envío de notificación push cuando Firebase no está configurado
     */
    private void simularEnvio(String token, String titulo, String mensaje) {
        logger.info("=== SIMULACIÓN DE PUSH NOTIFICATION ===");
        logger.info("Token: {}...", token != null ? token.substring(0, Math.min(20, token.length())) : "null");
        logger.info("Título: {}", titulo);
        logger.info("Mensaje: {}", mensaje);
        logger.info("========================================");
    }
}
