package com.unomas.factory;

import com.unomas.strategy.emparejamiento.EmparejamientoStrategy;
import com.unomas.strategy.emparejamiento.TipoEstrategia;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
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
    
    // EnumMap para búsqueda eficiente y type-safe
    private final EnumMap<TipoEstrategia, EmparejamientoStrategy> strategiesMap = 
            new EnumMap<>(TipoEstrategia.class);
    
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
     * Cada estrategia se registra automáticamente con su tipo enum
     */
    @PostConstruct
    public void init() {
        // Registrar cada estrategia con su tipo enum
        for (EmparejamientoStrategy strategy : strategies) {
            strategiesMap.put(strategy.getTipo(), strategy);
        }
        
        // Establecer estrategia por defecto (la primera con tipo NIVEL_HABILIDAD)
        estrategiaPorDefecto = strategiesMap.get(TipoEstrategia.NIVEL_HABILIDAD);
        
        // Si no existe, usar la primera disponible
        if (estrategiaPorDefecto == null && !strategies.isEmpty()) {
            estrategiaPorDefecto = strategies.get(0);
        }
    }
    
    /**
     * Crea una estrategia de emparejamiento basada en el tipo enum
     * 
     * @param tipo TipoEstrategia enum value
     * @return EmparejamientoStrategy correspondiente
     */
    public EmparejamientoStrategy crearEstrategia(TipoEstrategia tipo) {
        if (tipo == null) {
            return estrategiaPorDefecto;
        }
        
        return strategiesMap.getOrDefault(tipo, estrategiaPorDefecto);
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
     * @return Map con TipoEstrategia -> EmparejamientoStrategy
     */
    public Map<TipoEstrategia, EmparejamientoStrategy> obtenerEstrategiasDisponibles() {
        return new EnumMap<>(strategiesMap);
    }
}
