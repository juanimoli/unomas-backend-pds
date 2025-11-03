package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: Cancelado
 * El partido fue cancelado antes de su inicio
 * Patrón State
 */
public class CanceladoState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        throw new IllegalStateException("El partido ha sido cancelado");
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("El partido ha sido cancelado");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("El partido ha sido cancelado");
    }

    @Override
    public void finalizar(Partido partido) {
        throw new IllegalStateException("El partido ha sido cancelado");
    }

    @Override
    public void cancelar(Partido partido) {
        System.out.println("El partido ya está cancelado");
    }

    @Override
    public String getNombre() {
        return "CANCELADO";
    }
}
