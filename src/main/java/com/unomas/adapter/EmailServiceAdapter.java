package com.unomas.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${spring.mail.username:noreply@unomas.com}")
    private String senderEmail;
    
    @Override
    public void enviarNotificacion(String destinatario, String titulo, String mensaje) {
        if (!isDisponible()) {
            logger.warn("Servicio de email no disponible");
            return;
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(titulo);
            helper.setText(mensaje, false);
            helper.setFrom(senderEmail);
            
            mailSender.send(mimeMessage);
            logger.info("Email enviado a: {}", destinatario);
            
        } catch (MessagingException e) {
            logger.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
        }
    }
    
    @Override
    public boolean isDisponible() {
        return mailSender != null;
    }
}
