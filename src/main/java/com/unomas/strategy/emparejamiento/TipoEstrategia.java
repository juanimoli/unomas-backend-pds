package com.unomas.strategy.emparejamiento;

/**
 * Constantes para los tipos de estrategias de emparejamiento disponibles
 * Usando Strings en lugar de Enum para mayor flexibilidad
 */
public class TipoEstrategia {
    
    public static final String NIVEL_HABILIDAD = "NIVEL_HABILIDAD";
    public static final String CERCANIA = "CERCANIA";
    public static final String HISTORIAL = "HISTORIAL";
    
    private TipoEstrategia() {
        // Clase de utilidad - no instanciable
    }
    
    public static String getDescripcion(String tipo) {
        return switch (tipo) {
            case NIVEL_HABILIDAD -> "Nivel de Habilidad";
            case CERCANIA -> "Cercanía Geográfica";
            case HISTORIAL -> "Historial de Partidos";
            default -> "Desconocido";
        };
    }
}
