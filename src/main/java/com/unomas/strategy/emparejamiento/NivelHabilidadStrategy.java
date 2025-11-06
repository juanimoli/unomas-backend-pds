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
        // Si el partido permite cualquier nivel, siempre es compatible
        if (partido.isPermiteCualquierNivel()) {
            return true;
        }
        
        // Si hay nivel mínimo y el usuario no lo cumple
        if (partido.getNivelMinimoRequerido() != null) {
            if (compararNiveles(usuario.getNivelJuego(), partido.getNivelMinimoRequerido()) < 0) {
                return false;
            }
        }
        
        // Si hay nivel máximo y el usuario lo excede
        if (partido.getNivelMaximoRequerido() != null) {
            if (compararNiveles(usuario.getNivelJuego(), partido.getNivelMaximoRequerido()) > 0) {
                return false;
            }
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
        
        // Bonus si el deporte coincide con el favorito del usuario
        double score = 50.0;
        if (partido.getTipoDeporte() == usuario.getDeporteFavorito()) {
            score += 25.0;
        }
        
        // Calcular similitud de nivel
        if (!partido.isPermiteCualquierNivel()) {
            // Obtener el nivel "ideal" del partido (promedio de jugadores actuales)
            double nivelPromedio = calcularNivelPromedio(partido);
            double nivelUsuario = getNivelNumerico(usuario.getNivelJuego());
            
            // Mientras más cercano al promedio, mejor
            double diferencia = Math.abs(nivelPromedio - nivelUsuario);
            score += Math.max(0, 25.0 - (diferencia * 12.5)); // Max 25 puntos
        } else {
            score += 15.0; // Bonus menor si acepta cualquier nivel
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
    
    /**
     * Compara dos niveles de juego
     * @return -1 si nivel1 < nivel2, 0 si son iguales, 1 si nivel1 > nivel2
     */
    private int compararNiveles(Usuario.NivelJuego nivel1, Usuario.NivelJuego nivel2) {
        return Integer.compare(getNivelNumerico(nivel1), getNivelNumerico(nivel2));
    }
    
    /**
     * Convierte nivel de juego a valor numérico
     */
    private int getNivelNumerico(Usuario.NivelJuego nivel) {
        return switch (nivel) {
            case PRINCIPIANTE -> 1;
            case INTERMEDIO -> 2;
            case AVANZADO -> 3;
        };
    }
    
    /**
     * Calcula el nivel promedio de los jugadores del partido
     */
    private double calcularNivelPromedio(Partido partido) {
        if (partido.getJugadores().isEmpty()) {
            // Si no hay jugadores, usar el nivel del organizador
            return getNivelNumerico(partido.getOrganizador().getNivelJuego());
        }
        
        double suma = partido.getJugadores().stream()
            .mapToInt(j -> getNivelNumerico(j.getNivelJuego()))
            .average()
            .orElse(2.0); // Por defecto, nivel intermedio
        
        return suma;
    }
}
