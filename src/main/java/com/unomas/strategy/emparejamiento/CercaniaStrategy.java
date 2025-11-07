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
        if (usuario.getUbicacion() == null || partido.getUbicacion() == null) {
            return true;
        }
        
        return usuario.getUbicacion().calcularDistancia(partido.getUbicacion()) <= DISTANCIA_MAXIMA_KM;
    }
    
    @Override
    public List<Usuario> ordenarPorCompatibilidad(List<Usuario> usuarios, Partido partido) {
        List<Usuario> resultado = new ArrayList<>(usuarios);
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
            return 50.0;
        }
        
        double distancia = usuario.getUbicacion().calcularDistancia(partido.getUbicacion());
        
        if (distancia <= 5.0) return 100.0;
        if (distancia <= 10.0) return 85.0;
        if (distancia <= 20.0) return 70.0;
        if (distancia <= 35.0) return 50.0;
        if (distancia <= DISTANCIA_MAXIMA_KM) return 30.0;
        
        return 0.0;
    }
    
    @Override
    public String getNombre() {
        return "Cercanía Geográfica";
    }
    
    @Override
    public TipoEstrategia getTipo() {
        return TipoEstrategia.CERCANIA;
    }
    
    private double calcularDistanciaSegura(Ubicacion ubicacion1, Ubicacion ubicacion2) {
        if (ubicacion1 == null || ubicacion2 == null) {
            return Double.MAX_VALUE;
        }
        return ubicacion1.calcularDistancia(ubicacion2);
    }
}
