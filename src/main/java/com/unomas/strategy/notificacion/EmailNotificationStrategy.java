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
            return;
        }
        
        String asunto = construirAsunto(partido);
        String mensaje = construirMensaje(usuario, partido);
        
        try {
            emailAdapter.enviarNotificacion(usuario.getEmail(), asunto, mensaje);
        } catch (Exception e) {
            logger.error("Error enviando email a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }
    
    private String construirAsunto(Partido partido) {
        return "Actualización del Partido - " + partido.getTipoDeporte().getNombre();
    }
    
    private String construirMensaje(Usuario destinatario, Partido partido) {
        String emoji = obtenerEmojiDeporte(partido.getTipoDeporte().name());
        String estadoTexto = obtenerTextoEstado(partido.getEstadoActual());
        
        StringBuilder jugadoresInfo = new StringBuilder();
        jugadoresInfo.append("<ul style='list-style: none; padding: 0;'>");
        for (Usuario jugador : partido.getJugadores()) {
            jugadoresInfo.append(String.format(
                "<li style='padding: 5px 0;'>👤 <strong>%s</strong> - Nivel: %s</li>",
                jugador.getNombreUsuario(),
                jugador.getNivelJuego()
            ));
        }
        jugadoresInfo.append("</ul>");
        
        return String.format("""
            <div style="font-family: Arial, sans-serif;">
                <p style="font-size: 16px; color: #333;">
                    <strong>Hola %s,</strong>
                </p>
                
                <p style="font-size: 14px; color: #555; line-height: 1.6;">
                    %s
                </p>
                
                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;">
                    <h3 style="margin-top: 0; color: #667eea;">%s Detalles del Partido</h3>
                    
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px 0; color: #666;"><strong>🏆 Deporte:</strong></td>
                            <td style="padding: 8px 0; color: #333;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #666;"><strong>📅 Fecha y hora:</strong></td>
                            <td style="padding: 8px 0; color: #333;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #666;"><strong>📍 Ubicación:</strong></td>
                            <td style="padding: 8px 0; color: #333;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #666;"><strong>🎯 Estado:</strong></td>
                            <td style="padding: 8px 0;"><span style="background: #667eea; color: white; padding: 3px 10px; border-radius: 12px; font-size: 12px;">%s</span></td>
                        </tr>
                        <tr>
                            <td style="padding: 8px 0; color: #666;"><strong>👥 Jugadores:</strong></td>
                            <td style="padding: 8px 0; color: #333;">%d/%d inscritos</td>
                        </tr>
                    </table>
                </div>
                
                <div style="background: #fff; padding: 15px; border-left: 4px solid #667eea; margin: 20px 0;">
                    <h4 style="margin-top: 0; color: #667eea;">Equipo Actual:</h4>
                    %s
                </div>
                
                <p style="font-size: 14px; color: #555; margin-top: 20px;">
                    ¡Nos vemos en la cancha! 🙌
                </p>
                
                <p style="font-size: 12px; color: #999; margin-top: 30px;">
                    <em>— Equipo Uno Más</em>
                </p>
            </div>
            """,
            destinatario.getNombreUsuario(),
            estadoTexto,
            emoji,
            partido.getTipoDeporte().getNombre(),
            partido.getFechaHora(),
            partido.getDireccion() != null ? partido.getDireccion() : "Ver en app",
            partido.getEstadoActual(),
            partido.getJugadores().size(),
            partido.getCantidadJugadoresRequeridos(),
            jugadoresInfo.toString()
        );
    }
    
    private String obtenerEmojiDeporte(String deporte) {
        return switch (deporte) {
            case "FUTBOL" -> "⚽";
            case "BASQUET" -> "🏀";
            case "TENIS" -> "🎾";
            case "VOLEY" -> "🏐";
            case "PADEL" -> "🎾";
            default -> "🏃";
        };
    }
    
    private String obtenerTextoEstado(String estado) {
        return switch (estado) {
            case "BUSCANDO_JUGADORES" -> "🔍 Estamos buscando más jugadores para completar el equipo.";
            case "PARTIDO_ARMADO" -> "✅ ¡Equipo completo! El partido está listo para ser confirmado.";
            case "CONFIRMADO" -> "🎉 El partido ha sido confirmado por el organizador.";
            case "EN_JUEGO" -> "🔥 ¡El partido está en curso! Que lo disfrutes.";
            case "FINALIZADO" -> "🏁 El partido ha finalizado. ¡Esperamos que lo hayas disfrutado!";
            case "CANCELADO" -> "❌ Lamentablemente, el partido ha sido cancelado.";
            default -> "📢 Hay una actualización sobre tu partido.";
        };
    }
}
