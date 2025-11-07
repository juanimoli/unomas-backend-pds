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
    }

    @Override
    public void iniciar(Partido partido) {
        partido.cambiarEstado(new EnJuegoState());
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("No se puede finalizar un partido que no ha iniciado");
    }

    @Override
    public void cancelar(Partido partido) {
        partido.cambiarEstado(new CanceladoState());
    }

    @Override
    public String getNombre() {
        return "CONFIRMADO";
    }
}
