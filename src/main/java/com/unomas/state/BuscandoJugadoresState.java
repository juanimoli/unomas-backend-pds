package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Estado: Buscando Jugadores
 * Estado inicial cuando se crea un partido
 * Patrón State
 */
public class BuscandoJugadoresState implements EstadoPartido {

    @Override
    public void equipoCompleto(Partido partido) {
        // Transición a Partido Armado
        partido.cambiarEstado(new PartidoArmadoState());
    }

    @Override
    public void confirmar(Partido partido) {
        throw new IllegalStateException("No se puede confirmar un partido sin jugadores suficientes");
    }

    @Override
    public void iniciar(Partido partido) {
        throw new IllegalStateException("No se puede iniciar un partido sin jugadores suficientes");
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
        return "BUSCANDO_JUGADORES";
    }
}
