package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: En Juego
 * El partido ha comenzado
 * Patrón State
 */
public class EnJuegoState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        // El partido ya está en curso
    }

    @Override
    public void confirmar(Partido partido) {
        // Ya está en juego
    }

    @Override
    public void iniciar(Partido partido) {
    }

    @Override
    public void finalizar(Partido partido) {
        // Transición a Finalizado
        partido.cambiarEstado(new FinalizadoState());
    }

    @Override
    public void cancelar(Partido partido) {
        throw new IllegalStateException("No se puede cancelar un partido que ya está en juego");
    }

    @Override
    public String getNombre() {
        return "EN_JUEGO";
    }
}
