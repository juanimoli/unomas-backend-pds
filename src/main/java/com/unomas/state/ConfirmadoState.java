package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: Confirmado
 * Todos los jugadores aceptaron la participación
 * Patrón State
 */
public class ConfirmadoState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        // Ya está completo y confirmado
    }

    @Override
    public void confirmar(Partido partido) {
        // Ya está confirmado
        System.out.println("El partido ya está confirmado");
    }

    @Override
    public void iniciar(Partido partido) {
        // Transición a En Juego
        partido.cambiarEstado(new EnJuegoState());
        System.out.println("Partido " + partido.getId() + " - Ha comenzado. Estado: EN_JUEGO");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("No se puede finalizar un partido que no ha iniciado");
    }

    @Override
    public void cancelar(Partido partido) {
        partido.cambiarEstado(new CanceladoState());
        System.out.println("Partido " + partido.getId() + " cancelado desde estado CONFIRMADO");
    }

    @Override
    public String getNombre() {
        return "CONFIRMADO";
    }
}
