package com.unomas.strategy.emparejamiento;

import com.unomas.model.Partido;
import com.unomas.model.Ubicacion;
import com.unomas.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Estrategia de emparejamiento por cercanía geográfica
 * Patrón Strategy
 */
@Component
public class CercaniaStrategy implements EmparejamientoStrategy {
    
    private static final double DISTANCIA_MAXIMA_KM = 50.0; // 50 km
    
    @Override
    public boolean esCompatible(Usuario usuario, Partido partido) {
        // Si no hay información de ubicación, se considera compatible
        if (usuario.getUbicacion() == null || partido.getUbicacion() == null) {
            return true;
        }
        
        try {
            double distancia = calcularDistancia(usuario.getUbicacion(), partido.getUbicacion());
            return distancia <= DISTANCIA_MAXIMA_KM;
        } catch (Exception e) {
            // Si hay error al calcular distancia, aceptar por defecto
            return true;
        }
    }
    
    @Override
    public List<Usuario> ordenarPorCompatibilidad(List<Usuario> usuarios, Partido partido) {
        List<Usuario> resultado = new ArrayList<>(usuarios);
        
        // Ordenar por distancia (más cercanos primero)
        resultado.sort((u1, u2) -> {
            double dist1 = calcularDistanciaSegura(u1.getUbicacion(), partido.getUbicacion());
            double dist2 = calcularDistanciaSegura(u2.getUbicacion(), partido.getUbicacion());
            return Double.compare(dist1, dist2);
        });
        
        return resultado;
    }
    
    @Override
    public double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (usuario.getUbicacion() == null || partido.getUbicacion() == null) {
            return 50.0; // Compatibilidad media si no hay ubicación
        }
        
        try {
            double distancia = calcularDistancia(usuario.getUbicacion(), partido.getUbicacion());
            
            // Score basado en distancia (más cerca = mayor score)
            if (distancia <= 5.0) {
                return 100.0; // Muy cerca
            } else if (distancia <= 10.0) {
                return 85.0; // Cerca
            } else if (distancia <= 20.0) {
                return 70.0; // Distancia media
            } else if (distancia <= 35.0) {
                return 50.0; // Algo lejos
            } else if (distancia <= DISTANCIA_MAXIMA_KM) {
                return 30.0; // Lejos pero aceptable
            } else {
                return 0.0; // Demasiado lejos
            }
        } catch (Exception e) {
            return 50.0;
        }
    }
    
    @Override
    public String getNombre() {
        return "Cercanía Geográfica";
    }
    
    @Override
    public TipoEstrategia getTipo() {
        return TipoEstrategia.CERCANIA;
    }
    
    /**
     * Calcula la distancia entre dos ubicaciones usando el método de Ubicacion
     * @param ubicacion1 Primera ubicación
     * @param ubicacion2 Segunda ubicación
     * @return Distancia en kilómetros
     */
    private double calcularDistancia(Ubicacion ubicacion1, Ubicacion ubicacion2) {
        if (ubicacion1 == null || ubicacion2 == null) {
            throw new IllegalArgumentException("Las ubicaciones no pueden ser nulas");
        }
        return ubicacion1.calcularDistancia(ubicacion2);
    }
    
    /**
     * Versión segura que retorna infinito si hay error
     */
    private double calcularDistanciaSegura(Ubicacion ubicacion1, Ubicacion ubicacion2) {
        try {
            if (ubicacion1 == null || ubicacion2 == null) {
                return Double.MAX_VALUE;
            }
            return calcularDistancia(ubicacion1, ubicacion2);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
}
