package com.unomas.state;

import com.unomas.model.Partido;

/**
 * Interfaz base para el Patrón State
 * Define las operaciones que pueden realizarse en cada estado del partido
 */
public interface EstadoPartido {

    /**
     * Maneja cuando el equipo se completa
     */
    void equipoCompleto(Partido partido);

    /**
     * Confirma el partido
     */
    void confirmar(Partido partido);

    /**
     * Inicia el partido
     */
    void iniciar(Partido partido);

    /**
     * Finaliza el partido
     */
    void finalizar(Partido partido);

    /**
     * Cancela el partido
     */
    void cancelar(Partido partido);

    /**
     * Obtiene el nombre del estado
     */
    String getNombre();

    /**
     * Factory method para crear estados desde String
     */
    static EstadoPartido fromString(String estado) {
        return switch (estado) {
            case "BUSCANDO_JUGADORES", "NECESITAMOS_JUGADORES" -> new BuscandoJugadoresState();
            case "PARTIDO_ARMADO" -> new PartidoArmadoState();
            case "CONFIRMADO" -> new ConfirmadoState();
            case "EN_JUEGO" -> new EnJuegoState();
            case "FINALIZADO" -> new FinalizadoState();
            case "CANCELADO" -> new CanceladoState();
            default -> new BuscandoJugadoresState();
        };
    }
}
