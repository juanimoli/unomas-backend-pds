package com.unomas.strategy.emparejamiento;

import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia de emparejamiento por nivel de habilidad
 * Patrón Strategy
 */
@Component
public class NivelHabilidadStrategy implements EmparejamientoStrategy {
    
    @Override
    public boolean esCompatible(Usuario usuario, Partido partido) {
        if (partido.isPermiteCualquierNivel()) {
            return true;
        }
        
        int nivelUsuario = getNivelNumerico(usuario.getNivelJuego());
        
        if (partido.getNivelMinimoRequerido() != null 
            && nivelUsuario < getNivelNumerico(partido.getNivelMinimoRequerido())) {
            return false;
        }
        
        if (partido.getNivelMaximoRequerido() != null 
            && nivelUsuario > getNivelNumerico(partido.getNivelMaximoRequerido())) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<Usuario> ordenarPorCompatibilidad(List<Usuario> usuarios, Partido partido) {
        List<Usuario> resultado = new ArrayList<>(usuarios);
        
        // Ordenar por compatibilidad (mayor score primero)
        resultado.sort(Comparator.comparingDouble((Usuario u) -> calcularCompatibilidad(u, partido)).reversed());
        
        return resultado;
    }
    
    @Override
    public double calcularCompatibilidad(Usuario usuario, Partido partido) {
        if (!esCompatible(usuario, partido)) {
            return 0.0;
        }
        
        double score = partido.getTipoDeporte() == usuario.getDeporteFavorito() ? 75.0 : 50.0;
        
        if (!partido.isPermiteCualquierNivel()) {
            double nivelPromedio = calcularNivelPromedio(partido);
            double diferencia = Math.abs(nivelPromedio - getNivelNumerico(usuario.getNivelJuego()));
            score += Math.max(0, 25.0 - (diferencia * 12.5));
        } else {
            score += 15.0;
        }
        
        return Math.min(100.0, score);
    }
    
    @Override
    public String getNombre() {
        return "Nivel de Habilidad";
    }
    
    @Override
    public TipoEstrategia getTipo() {
        return TipoEstrategia.NIVEL_HABILIDAD;
    }
    
    private int getNivelNumerico(Usuario.NivelJuego nivel) {
        return switch (nivel) {
            case PRINCIPIANTE -> 1;
            case INTERMEDIO -> 2;
            case AVANZADO -> 3;
        };
    }
    
    private double calcularNivelPromedio(Partido partido) {
        if (partido.getJugadores().isEmpty()) {
            return getNivelNumerico(partido.getOrganizador().getNivelJuego());
        }
        
        return partido.getJugadores().stream()
            .mapToInt(j -> getNivelNumerico(j.getNivelJuego()))
            .average()
            .orElse(2.0);
    }
}
