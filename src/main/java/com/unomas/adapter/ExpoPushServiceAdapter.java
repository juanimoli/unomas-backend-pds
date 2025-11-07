package com.unomas.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter para enviar notificaciones push usando Expo Push Notification Service
 * Esto es necesario cuando se usa Expo Go en dispositivos iOS sin cuenta de desarrollador
 * 
 * Documentación: https://docs.expo.dev/push-notifications/sending-notifications/
 */
@Component
public class ExpoPushServiceAdapter implements NotificacionServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpoPushServiceAdapter.class);
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    
    @Value("${expo.push.enabled:true}")
    private boolean expoPushEnabled;
    
    private final RestTemplate restTemplate;
    
    public ExpoPushServiceAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public void enviarNotificacion(String expoToken, String titulo, String mensaje) {
        if (!isDisponible() || expoToken == null || !expoToken.startsWith("ExponentPushToken[")) {
            logger.warn("Expo Push no disponible o token inválido");
            return;
        }
        
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("to", expoToken);
            notification.put("title", titulo);
            notification.put("body", mensaje);
            notification.put("sound", "default");
            notification.put("priority", "high");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                EXPO_PUSH_URL, request, (Class<Map<String, Object>>)(Class<?>)Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Notificación push enviada a: {}", expoToken.substring(0, 30) + "...");
            }
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación push: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean isDisponible() {
        return expoPushEnabled;
    }
}
