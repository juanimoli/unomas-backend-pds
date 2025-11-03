package com.unomas.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Adapter para el servicio de Email usando JavaMail
 * Patrón Adapter: Adapta JavaMailSender a NotificacionServiceAdapter
 */
@Component
public class EmailServiceAdapter implements NotificacionServiceAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceAdapter.class);
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Override
    public void enviarNotificacion(String destinatario, String titulo, String mensaje) {
        if (!isDisponible()) {
            logger.warn("Servicio de email no disponible. Simulando envío a: {}", destinatario);
            simularEnvio(destinatario, titulo, mensaje);
            return;
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(titulo);
            helper.setText(mensaje, false);
            helper.setFrom("noreply@unomas.com");
            
            mailSender.send(mimeMessage);
            logger.info("Email enviado exitosamente a: {}", destinatario);
            
        } catch (MessagingException e) {
            logger.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
            // En caso de error, simular el envío para fines de demostración
            simularEnvio(destinatario, titulo, mensaje);
        }
    }
    
    @Override
    public boolean isDisponible() {
        return mailSender != null;
    }
    
    /**
     * Simula el envío de email cuando el servicio no está configurado
     */
    private void simularEnvio(String destinatario, String titulo, String mensaje) {
        logger.info("=== SIMULACIÓN DE EMAIL ===");
        logger.info("Para: {}", destinatario);
        logger.info("Asunto: {}", titulo);
        logger.info("Mensaje: {}", mensaje.substring(0, Math.min(100, mensaje.length())) + "...");
        logger.info("===========================");
    }
}
