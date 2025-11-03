package com.unomas.strategy;

import com.unomas.model.Partido;
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
    
    private static final double RADIO_TIERRA_KM = 6371.0;
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
    
    /**
     * Calcula la distancia entre dos puntos usando la fórmula de Haversine
     * @param ubicacion1 "latitud,longitud"
     * @param ubicacion2 "latitud,longitud"
     * @return Distancia en kilómetros
     */
    private double calcularDistancia(String ubicacion1, String ubicacion2) {
        String[] coords1 = ubicacion1.split(",");
        String[] coords2 = ubicacion2.split(",");
        
        double lat1 = Double.parseDouble(coords1[0].trim());
        double lon1 = Double.parseDouble(coords1[1].trim());
        double lat2 = Double.parseDouble(coords2[0].trim());
        double lon2 = Double.parseDouble(coords2[1].trim());
        
        return calcularDistanciaHaversine(lat1, lon1, lat2, lon2);
    }
    
    /**
     * Versión segura que retorna infinito si hay error
     */
    private double calcularDistanciaSegura(String ubicacion1, String ubicacion2) {
        try {
            return calcularDistancia(ubicacion1, ubicacion2);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
    
    /**
     * Fórmula de Haversine para calcular distancia entre coordenadas
     */
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RADIO_TIERRA_KM * c;
    }
}
