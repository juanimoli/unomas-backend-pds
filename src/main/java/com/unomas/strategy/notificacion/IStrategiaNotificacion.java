package com.unomas.strategy.notificacion;

import com.unomas.model.Partido;
import com.unomas.model.Usuario;

/**
 * Interfaz de Strategy para diferentes estrategias de notificación.
 * Permite implementar distintos mecanismos de notificación (email, push, etc.)
 * 
 * Patrón: Strategy
 * Contexto: PartidoListener usa esta estrategia para enviar notificaciones
 */
public interface IStrategiaNotificacion {
    
    /**
     * Envía una notificación a un usuario sobre cambios en un partido.
     * @param usuario el usuario destinatario de la notificación
     * @param partido el partido sobre el cual notificar
     */
    void enviarNotificacion(Usuario usuario, Partido partido);
}
