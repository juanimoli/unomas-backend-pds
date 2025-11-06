package com.unomas.strategy.notificacion;

import com.unomas.adapter.NotificacionServiceAdapter;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Estrategia concreta para notificaciones por Email.
 * Implementa IStrategiaNotificacion usando el patrón Strategy.
 * 
 * Patrón: Strategy + Adapter
 * Rol Strategy: Concrete Strategy para notificaciones email
 * Rol Adapter: Usa NotificacionServiceAdapter para enviar emails
 */
public class EmailNotificationStrategy implements IStrategiaNotificacion {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationStrategy.class);
    
    private final NotificacionServiceAdapter emailAdapter;
    
    public EmailNotificationStrategy(NotificacionServiceAdapter emailAdapter) {
        this.emailAdapter = emailAdapter;
    }
    
    @Override
    public void enviarNotificacion(Usuario usuario, Partido partido) {
        if (!usuario.isNotificacionesEmail()) {
            logger.debug("Usuario {} tiene notificaciones email desactivadas", usuario.getNombreUsuario());
            return;
        }
        
        logger.info("Enviando notificación por EMAIL a {} para partido {}", 
                   usuario.getEmail(), partido.getId());
        
        String asunto = construirAsunto(partido);
        String cuerpo = construirMensajeEmail(partido, usuario);
        
        try {
            emailAdapter.enviarNotificacion(
                usuario.getEmail(),
                asunto,
                cuerpo
            );
            logger.info("Email enviado exitosamente a: {}", usuario.getEmail());
        } catch (Exception e) {
            logger.error("Error al enviar email a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }
    
    private String construirAsunto(Partido partido) {
        return "Actualización del Partido - " + partido.getTipoDeporte().getNombre();
    }
    
    private String construirMensajeEmail(Partido partido, Usuario destinatario) {
        return String.format("""
            Hola %s,
            
            Te notificamos sobre cambios en el partido al que estás inscrito.
            
            Detalles del partido:
            - Deporte: %s
            - Fecha y hora: %s
            - Ubicación: %s
            - Estado: %s
            - Jugadores: %d/%d
            
            ¡Nos vemos en la cancha!
            
            Equipo Uno Mas
            """,
            destinatario.getNombreUsuario(),
            partido.getTipoDeporte().getNombre(),
            partido.getFechaHora(),
            partido.getDireccion() != null ? partido.getDireccion() : partido.getUbicacion(),
            partido.getEstadoActual(),
            partido.getJugadores().size(),
            partido.getCantidadJugadoresRequeridos()
        );
    }
}
