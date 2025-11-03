package com.unomas.observer;

import com.unomas.adapter.NotificacionServiceAdapter;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer concreto para notificaciones Push (Firebase)
 * Patrón Observer + Adapter
 */
public class PushNotificationObserver implements NotificacionObserver {
    
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationObserver.class);
    
    private final NotificacionServiceAdapter firebaseAdapter;
    
    public PushNotificationObserver(NotificacionServiceAdapter firebaseAdapter) {
        this.firebaseAdapter = firebaseAdapter;
    }
    
    @Override
    public void actualizar(Partido partido, String mensaje) {
        logger.info("Enviando notificaciones PUSH para partido {}", partido.getId());
        
        // Enviar push a todos los jugadores que tienen activadas las notificaciones push
        for (Usuario jugador : partido.getJugadores()) {
            if (jugador.isNotificacionesPush() && jugador.getFirebaseToken() != null) {
                String titulo = "Partido " + partido.getTipoDeporte().getNombre();
                String cuerpo = construirMensajePush(partido, mensaje);
                
                try {
                    firebaseAdapter.enviarNotificacion(
                        jugador.getFirebaseToken(),
                        titulo,
                        cuerpo
                    );
                    logger.info("Push enviado a usuario: {}", jugador.getNombreUsuario());
                } catch (Exception e) {
                    logger.error("Error al enviar push a {}: {}", jugador.getNombreUsuario(), e.getMessage());
                }
            }
        }
        
        // También notificar al organizador
        Usuario organizador = partido.getOrganizador();
        if (organizador != null && 
            organizador.isNotificacionesPush() && 
            organizador.getFirebaseToken() != null &&
            !partido.getJugadores().contains(organizador)) {
            
            String titulo = "Tu Partido - " + partido.getTipoDeporte().getNombre();
            String cuerpo = construirMensajePush(partido, mensaje);
            
            try {
                firebaseAdapter.enviarNotificacion(
                    organizador.getFirebaseToken(),
                    titulo,
                    cuerpo
                );
                logger.info("Push enviado al organizador: {}", organizador.getNombreUsuario());
            } catch (Exception e) {
                logger.error("Error al enviar push al organizador: {}", e.getMessage());
            }
        }
    }
    
    private String construirMensajePush(Partido partido, String mensaje) {
        return String.format("%s - %s (%d/%d jugadores)",
            mensaje,
            partido.getEstadoActual(),
            partido.getJugadores().size(),
            partido.getCantidadJugadoresRequeridos()
        );
    }
    
    @Override
    public String getTipo() {
        return "PUSH";
    }
}
