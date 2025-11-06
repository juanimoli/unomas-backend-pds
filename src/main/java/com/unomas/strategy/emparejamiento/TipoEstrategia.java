package com.unomas.strategy.emparejamiento;

/**
 * Enum que define los tipos de estrategias de emparejamiento disponibles
 * Proporciona identificadores únicos y type-safety
 */
public enum TipoEstrategia {
    NIVEL_HABILIDAD("Nivel de Habilidad"),
    CERCANIA("Cercanía Geográfica"),
    HISTORIAL("Historial de Partidos");
    
    private final String descripcion;
    
    TipoEstrategia(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
