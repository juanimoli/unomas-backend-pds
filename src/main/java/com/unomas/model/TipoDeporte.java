package com.unomas.model;

/**
 * Constantes para los tipos de deportes disponibles
 * Usando Strings en lugar de Enum para mayor flexibilidad
 */
public class TipoDeporte {
    
    // Constantes de deportes
    public static final String FUTBOL = "FUTBOL";
    public static final String FUTBOL_5 = "FUTBOL_5";
    public static final String FUTBOL_7 = "FUTBOL_7";
    public static final String BASQUET = "BASQUET";
    public static final String VOLEY = "VOLEY";
    public static final String PADDLE = "PADDLE";
    public static final String TENIS = "TENIS";
    public static final String RUGBY = "RUGBY";
    public static final String HOCKEY = "HOCKEY";
    
    private TipoDeporte() {
        // Clase de utilidad - no instanciable
    }
    
    public static String getNombre(String tipo) {
        return switch (tipo) {
            case FUTBOL -> "Fútbol";
            case FUTBOL_5 -> "Fútbol 5";
            case FUTBOL_7 -> "Fútbol 7";
            case BASQUET -> "Básquet";
            case VOLEY -> "Vóley";
            case PADDLE -> "Paddle";
            case TENIS -> "Tenis";
            case RUGBY -> "Rugby";
            case HOCKEY -> "Hockey";
            default -> tipo;
        };
    }
    
    public static int getJugadoresDefault(String tipo) {
        return switch (tipo) {
            case FUTBOL -> 11;
            case FUTBOL_5 -> 5;
            case FUTBOL_7 -> 7;
            case BASQUET -> 5;
            case VOLEY -> 6;
            case PADDLE -> 4;
            case TENIS -> 2;
            case RUGBY -> 15;
            case HOCKEY -> 11;
            default -> 5;
        };
    }
}
