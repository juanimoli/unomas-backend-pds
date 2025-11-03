package com.unomas.observer;

import com.unomas.model.Partido;

/**
 * Interface Observer para el sistema de notificaciones
 * Patrón Observer
 */
public interface NotificacionObserver {
    
    /**
     * Actualiza al observer con información del partido y el mensaje
     */
    void actualizar(Partido partido, String mensaje);
    
    /**
     * Obtiene el tipo de notificación
     */
    String getTipo();
}
