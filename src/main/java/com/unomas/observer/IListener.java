package com.unomas.observer;

/**
 * Interfaz IListener del patrón Observer.
 * Define el contrato para objetos observadores que reciben notificaciones.
 * 
 * Patrón: Observer
 * Componente: Observer/Listener
 */
public interface IListener {
    
    /**
     * Método llamado cuando el observable notifica un cambio.
     * @param observable el objeto observable que generó la notificación
     */
    void notificar(Object observable);
}
