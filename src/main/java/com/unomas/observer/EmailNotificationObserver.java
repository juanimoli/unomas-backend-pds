package com.unomas.observer;

import com.unomas.adapter.NotificacionServiceAdapter;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observer concreto para notificaciones por Email
 * Patrón Observer + Adapter
 */
public class EmailNotificationObserver implements NotificacionObserver {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationObserver.class);
    
    private final NotificacionServiceAdapter emailAdapter;
    
    public EmailNotificationObserver(NotificacionServiceAdapter emailAdapter) {
        this.emailAdapter = emailAdapter;
    }
    
    @Override
    public void actualizar(Partido partido, String mensaje) {
        logger.info("Enviando notificaciones por EMAIL para partido {}", partido.getId());
        
        // Enviar email a todos los jugadores que tienen activadas las notificaciones por email
        for (Usuario jugador : partido.getJugadores()) {
            if (jugador.isNotificacionesEmail()) {
                String asunto = "Actualización del Partido - " + partido.getTipoDeporte().getNombre();
                String cuerpo = construirMensajeEmail(partido, mensaje, jugador);
                
                try {
                    emailAdapter.enviarNotificacion(
                        jugador.getEmail(),
                        asunto,
                        cuerpo
                    );
                    logger.info("Email enviado a: {}", jugador.getEmail());
                } catch (Exception e) {
                    logger.error("Error al enviar email a {}: {}", jugador.getEmail(), e.getMessage());
                }
            }
        }
        
        // También notificar al organizador si no está en la lista de jugadores
        Usuario organizador = partido.getOrganizador();
        if (organizador != null && 
            organizador.isNotificacionesEmail() && 
            !partido.getJugadores().contains(organizador)) {
            
            String asunto = "Tu Partido - " + partido.getTipoDeporte().getNombre();
            String cuerpo = construirMensajeEmail(partido, mensaje, organizador);
            
            try {
                emailAdapter.enviarNotificacion(
                    organizador.getEmail(),
                    asunto,
                    cuerpo
                );
                logger.info("Email enviado al organizador: {}", organizador.getEmail());
            } catch (Exception e) {
                logger.error("Error al enviar email al organizador: {}", e.getMessage());
            }
        }
    }
    
    private String construirMensajeEmail(Partido partido, String mensaje, Usuario destinatario) {
        return String.format("""
            Hola %s,
            
            %s
            
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
            mensaje,
            partido.getTipoDeporte().getNombre(),
            partido.getFechaHora(),
            partido.getDireccion() != null ? partido.getDireccion() : partido.getUbicacion(),
            partido.getEstadoActual(),
            partido.getJugadores().size(),
            partido.getCantidadJugadoresRequeridos()
        );
    }
    
    @Override
    public String getTipo() {
        return "EMAIL";
    }
}
