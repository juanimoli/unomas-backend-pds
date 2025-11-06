package com.unomas.observer;

/**
 * Interfaz IObservable del patrón Observer.
 * Define el contrato para objetos observables que pueden notificar a sus observadores.
 * 
 * Patrón: Observer
 * Componente: Subject/Observable
 */
public interface IObservable {
    
    /**
     * Agrega un observador a la lista de observadores.
     * @param observer el observador a agregar
     */
    void agregarObserver(IListener observer);
    
    /**
     * Elimina un observador de la lista de observadores.
     * @param observer el observador a eliminar
     */
    void eliminarObserver(IListener observer);
    
    /**
     * Notifica a todos los observadores registrados sobre un cambio de estado.
     */
    void notificarObservadores();
}
