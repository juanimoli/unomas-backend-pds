package com.unomas.adapter;

/**
 * Interface para adaptar diferentes servicios de notificación
 * Patrón Adapter
 */
public interface NotificacionServiceAdapter {
    
    /**
     * Envía una notificación
     * @param destinatario Email, token de Firebase, etc.
     * @param titulo Título de la notificación
     * @param mensaje Cuerpo del mensaje
     */
    void enviarNotificacion(String destinatario, String titulo, String mensaje);
    
    /**
     * Verifica si el servicio está disponible
     */
    boolean isDisponible();
}
