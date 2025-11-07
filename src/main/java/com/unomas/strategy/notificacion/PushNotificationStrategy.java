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
            return;
        }
        
        String pushToken = usuario.getFirebaseToken();
        if (pushToken == null || pushToken.isEmpty()) {
            logger.warn("Usuario {} no tiene token FCM configurado", usuario.getNombreUsuario());
            return;
        }
        
        String titulo = "Partido " + com.unomas.model.TipoDeporte.getNombre(partido.getTipoDeporte());
        String mensaje = construirMensajePush(partido);
        
        try {
            firebaseAdapter.enviarNotificacion(pushToken, titulo, mensaje);
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
