package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: Partido Armado
 * Cuando se alcanza el número requerido de jugadores
 * Patrón State
 */
public class PartidoArmadoState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        // Ya está completo, no hace nada
    }

    @Override
    public void confirmar(Partido partido) {
        // Transición a Confirmado
        partido.cambiarEstado(new ConfirmadoState());
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("Debe confirmarse el partido antes de iniciarlo");
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
        return "PARTIDO_ARMADO";
    }
}
