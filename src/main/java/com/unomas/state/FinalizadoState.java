package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: Finalizado
 * El partido ha concluido
 * Patrón State
 */
public class FinalizadoState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        throw new IllegalStateException("El partido ya ha finalizado");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("El partido ya ha finalizado");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("El partido ya ha finalizado");
    }

    @Override
    public void finalizar(Partido partido) {
    }

    @Override
    public void cancelar(Partido partido) {
        throw new IllegalStateException("No se puede cancelar un partido finalizado");
    }

    @Override
    public String getNombre() {
        return "FINALIZADO";
    }
}
