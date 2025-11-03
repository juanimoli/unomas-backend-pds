package com.unomas.observer;

import com.unomas.model.Partido;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base Observable para el patrón Observer
 * Los partidos heredan de esta clase para poder notificar cambios
 */
public abstract class PartidoObservable {
    
    private final List<NotificacionObserver> observers = new ArrayList<>();
    
    /**
     * Agrega un observer
     */
    public void agregarObserver(NotificacionObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Elimina un observer
     */
    public void eliminarObserver(NotificacionObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica a todos los observers
     */
    protected void notificarObservadores(String mensaje) {
        for (NotificacionObserver observer : observers) {
            observer.actualizar((Partido) this, mensaje);
        }
    }
    
    /**
     * Obtiene la lista de observers
     */
    public List<NotificacionObserver> getObservers() {
        return new ArrayList<>(observers);
    }
}
