package com.unomas.factory;

import com.unomas.strategy.emparejamiento.EmparejamientoStrategy;
import com.unomas.strategy.emparejamiento.TipoEstrategia;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory para crear estrategias de emparejamiento
 * Patrón Factory Method + Strategy
 * 
 * Descubre automáticamente todas las implementaciones de EmparejamientoStrategy
 * registradas en el contexto de Spring, sin necesidad de conocerlas explícitamente.
 * 
 * Esto permite agregar nuevas estrategias sin modificar este Factory (Open/Closed Principle)
 */
@Component
public class EmparejamientoStrategyFactory {
    
    // Spring inyecta automáticamente TODAS las implementaciones de EmparejamientoStrategy
    private final List<EmparejamientoStrategy> strategies;
    
    // Map para búsqueda por tipo (String)
    private final Map<String, EmparejamientoStrategy> strategiesMap = new HashMap<>();
    
    private EmparejamientoStrategy estrategiaPorDefecto;
    
    /**
     * Constructor con inyección automática de todas las estrategias
     * Spring encuentra todas las implementaciones de EmparejamientoStrategy anotadas con @Component
     * 
     * @param strategies Lista de todas las estrategias disponibles en el contexto
     */
    public EmparejamientoStrategyFactory(List<EmparejamientoStrategy> strategies) {
        this.strategies = strategies;
    }
    
    /**
     * Inicializa el mapa de estrategias después de la inyección de dependencias
     * Cada estrategia se registra automáticamente con su tipo String
     */
    @PostConstruct
    public void init() {
        // Registrar cada estrategia con su tipo String
        for (EmparejamientoStrategy strategy : strategies) {
            strategiesMap.put(strategy.getTipo(), strategy);
        }
        
        // Establecer estrategia por defecto (NIVEL_HABILIDAD)
        estrategiaPorDefecto = strategiesMap.get(TipoEstrategia.NIVEL_HABILIDAD);
        
        // Si no existe, usar la primera disponible
        if (estrategiaPorDefecto == null && !strategies.isEmpty()) {
            estrategiaPorDefecto = strategies.get(0);
        }
    }
    
    /**
     * Crea una estrategia de emparejamiento basada en el tipo String
     * 
     * @param tipo String tipo de estrategia (NIVEL_HABILIDAD, CERCANIA, HISTORIAL)
     * @return EmparejamientoStrategy correspondiente
     */
    public EmparejamientoStrategy crearEstrategia(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return estrategiaPorDefecto;
        }
        
        return strategiesMap.getOrDefault(tipo.trim().toUpperCase(), estrategiaPorDefecto);
    }
    
    /**
     * Obtiene la estrategia por defecto (Nivel de Habilidad)
     * 
     * @return EmparejamientoStrategy por defecto
     */
    public EmparejamientoStrategy crearEstrategiaPorDefecto() {
        return estrategiaPorDefecto;
    }
    
    /**
     * Obtiene todas las estrategias disponibles
     * 
     * @return Lista de todas las estrategias descubiertas por Spring
     */
    public List<EmparejamientoStrategy> obtenerTodasLasEstrategias() {
        return strategies;
    }
    
    /**
     * Obtiene información sobre todas las estrategias disponibles
     * 
     * @return Map con tipo String -> EmparejamientoStrategy
     */
    public Map<String, EmparejamientoStrategy> obtenerEstrategiasDisponibles() {
        return new HashMap<>(strategiesMap);
    }
}
