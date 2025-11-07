package com.unomas.strategy.notificacion;

import com.unomas.adapter.NotificacionServiceAdapter;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Estrategia concreta para notificaciones Push (Firebase).
 * Implementa IStrategiaNotificacion usando el patrón Strategy.
 * 
 * Patrón: Strategy + Adapter
 * Rol Strategy: Concrete Strategy para notificaciones push
 * Rol Adapter: Usa NotificacionServiceAdapter para enviar push notifications
 */
public class PushNotificationStrategy implements IStrategiaNotificacion {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationStrategy.class);
    
    private final NotificacionServiceAdapter firebaseAdapter;
    
    public PushNotificationStrategy(NotificacionServiceAdapter firebaseAdapter) {
        this.firebaseAdapter = firebaseAdapter;
    }
    
    @Override
    public void enviarNotificacion(Usuario usuario, Partido partido) {
        if (!usuario.isNotificacionesPush()) {
            logger.debug("Usuario {} tiene notificaciones push desactivadas", usuario.getNombreUsuario());
            return;
        }
        
        // Verificar si tiene Expo Push Token (token que comienza con ExponentPushToken[)
        // o Firebase token (para apps nativas)
        String pushToken = usuario.getFirebaseToken();
        if (pushToken == null || pushToken.isEmpty()) {
            logger.warn("Usuario {} no tiene push token configurado", usuario.getNombreUsuario());
            return;
        }
        
        logger.info("Enviando notificación PUSH a {} para partido {}", 
                   usuario.getNombreUsuario(), partido.getId());
        
        String titulo = "Partido " + partido.getTipoDeporte().getNombre();
        String cuerpo = construirMensajePush(partido);
        
        try {
            firebaseAdapter.enviarNotificacion(
                pushToken,  // Puede ser Expo token o Firebase token
                titulo,
                cuerpo
            );
            logger.info("Push enviado exitosamente a usuario: {}", usuario.getNombreUsuario());
        } catch (Exception e) {
            logger.error("Error al enviar push a {}: {}", usuario.getNombreUsuario(), e.getMessage());
        }
    }
    
    private String construirMensajePush(Partido partido) {
        return String.format("Estado: %s - Jugadores: %d/%d",
            partido.getEstadoActual(),
            partido.getJugadores().size(),
            partido.getCantidadJugadoresRequeridos()
        );
    }
}
