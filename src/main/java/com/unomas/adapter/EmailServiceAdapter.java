package com.unomas.adapter;

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
            return;
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(titulo);
            helper.setText(buildHtmlTemplate(titulo, mensaje), true);
            helper.setFrom(senderEmail, "Uno Más");
            
            mailSender.send(mimeMessage);
            
        } catch (Exception e) {
            logger.error("Error al enviar email: {}", e.getMessage());
        }
    }
    
    private String buildHtmlTemplate(String titulo, String mensaje) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background: #ffffff;
                        border-radius: 10px;
                        overflow: hidden;
                        box-shadow: 0 0 20px rgba(0,0,0,0.1);
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                        font-weight: 600;
                    }
                    .header .logo {
                        font-size: 48px;
                        margin-bottom: 10px;
                    }
                    .content {
                        padding: 30px;
                    }
                    .notification-box {
                        background: #f8f9fa;
                        border-left: 4px solid #667eea;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 5px;
                    }
                    .notification-box h2 {
                        margin-top: 0;
                        color: #667eea;
                        font-size: 20px;
                    }
                    .notification-box p {
                        margin: 10px 0;
                        font-size: 16px;
                        color: #555;
                    }
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 14px;
                        color: #777;
                        border-top: 1px solid #e0e0e0;
                    }
                    .footer p {
                        margin: 5px 0;
                    }
                    .footer a {
                        color: #667eea;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">⚽</div>
                        <h1>Uno Más</h1>
                        <p style="margin: 0; opacity: 0.9;">Tu plataforma de encuentros deportivos</p>
                    </div>
                    
                    <div class="content">
                        <div class="notification-box">
                            <h2>%s</h2>
                            <p>%s</p>
                        </div>
                        
                        <p style="margin-top: 30px; color: #666;">
                            Para más información sobre tus partidos, ingresa a la aplicación móvil Uno Más.
                        </p>
                    </div>
                    
                    <div class="footer">
                        <p><strong>Uno Más</strong> - Conectando deportistas</p>
                        <p>Este es un mensaje automático, por favor no respondas a este email.</p>
                    </div>
                </div>
            </body>
            </html>
            """, titulo, mensaje);
    }
    
    @Override
    public boolean isDisponible() {
        return mailSender != null;
    }
}
