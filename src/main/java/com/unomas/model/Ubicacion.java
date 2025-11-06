package com.unomas.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una ubicación geográfica
 * Usada como Embeddable en Usuario y Partido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Ubicacion {
    
    private Double longitud;
    private Double latitud;
    
    /**
     * Constructor a partir de String en formato "latitud,longitud"
     */
    public Ubicacion(String coordenadas) {
        if (coordenadas != null && coordenadas.contains(",")) {
            String[] parts = coordenadas.split(",");
            this.latitud = Double.parseDouble(parts[0].trim());
            this.longitud = Double.parseDouble(parts[1].trim());
        }
    }
    
    /**
     * Convierte a String en formato "latitud,longitud"
     */
    @Override
    public String toString() {
        return latitud + "," + longitud;
    }
    
    /**
     * Calcula la distancia a otra ubicación usando Haversine
     */
    public double calcularDistancia(Ubicacion otra) {
        if (otra == null) return Double.MAX_VALUE;
        
        final double RADIO_TIERRA_KM = 6371.0;
        
        double dLat = Math.toRadians(otra.latitud - this.latitud);
        double dLon = Math.toRadians(otra.longitud - this.longitud);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(this.latitud)) * Math.cos(Math.toRadians(otra.latitud)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RADIO_TIERRA_KM * c;
    }
}
