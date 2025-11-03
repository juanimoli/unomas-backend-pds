package com.unomas.model;

/**
 * Enum que representa los tipos de deportes disponibles
 */
public enum TipoDeporte {
    FUTBOL("Fútbol", 11),
    FUTBOL_5("Fútbol 5", 5),
    FUTBOL_7("Fútbol 7", 7),
    BASQUET("Básquet", 5),
    VOLEY("Vóley", 6),
    PADDLE("Paddle", 4),
    TENIS("Tenis", 2),
    RUGBY("Rugby", 15),
    HOCKEY("Hockey", 11);

    private final String nombre;
    private final int jugadoresDefault;

    TipoDeporte(String nombre, int jugadoresDefault) {
        this.nombre = nombre;
        this.jugadoresDefault = jugadoresDefault;
    }

    public String getNombre() {
        return nombre;
    }

    public int getJugadoresDefault() {
        return jugadoresDefault;
    }
}
