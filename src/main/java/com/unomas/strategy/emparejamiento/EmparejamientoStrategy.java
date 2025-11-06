package com.unomas.strategy.emparejamiento;

import com.unomas.model.Partido;
import com.unomas.model.Usuario;

import java.util.List;

/**
 * Interface Strategy para diferentes algoritmos de emparejamiento
 * Patrón Strategy
 */
public interface EmparejamientoStrategy {
    
    /**
     * Evalúa si un usuario es compatible con un partido
     * @param usuario Usuario a evaluar
     * @param partido Partido a evaluar
     * @return true si el usuario es compatible, false en caso contrario
     */
    boolean esCompatible(Usuario usuario, Partido partido);
    
    /**
     * Ordena una lista de usuarios según la compatibilidad con el partido
     * @param usuarios Lista de usuarios a ordenar
     * @param partido Partido de referencia
     * @return Lista ordenada de usuarios (más compatibles primero)
     */
    List<Usuario> ordenarPorCompatibilidad(List<Usuario> usuarios, Partido partido);
    
    /**
     * Calcula un score de compatibilidad (0-100)
     * @param usuario Usuario a evaluar
     * @param partido Partido a evaluar
     * @return Score de compatibilidad
     */
    double calcularCompatibilidad(Usuario usuario, Partido partido);
    
    /**
     * Obtiene el nombre de la estrategia
     */
    String getNombre();
    
    /**
     * Obtiene el tipo único de esta estrategia
     * @return TipoEstrategia enum value
     */
    TipoEstrategia getTipo();
}
