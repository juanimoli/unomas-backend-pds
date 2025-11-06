package com.unomas.strategy.emparejamiento;

import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Estrategia de emparejamiento por historial de partidos previos
 * Prioriza jugadores que han jugado juntos anteriormente
 * Patrón Strategy
 */
@Component
public class HistorialStrategy implements EmparejamientoStrategy {
    
    @Override
    public boolean esCompatible(Usuario usuario, Partido partido) {
        // Todos los usuarios son compatibles con esta estrategia
        // La compatibilidad se mide por el historial
        return true;
    }
    
    @Override
    public List<Usuario> ordenarPorCompatibilidad(List<Usuario> usuarios, Partido partido) {
        List<Usuario> resultado = new ArrayList<>(usuarios);
        
        // Ordenar por compatibilidad de historial (mayor score primero)
        resultado.sort(Comparator.comparingDouble((Usuario u) -> calcularCompatibilidad(u, partido)).reversed());
        
        return resultado;
    }
    
    @Override
    public double calcularCompatibilidad(Usuario usuario, Partido partido) {
        double score = 40.0; // Score base
        
        // Contar partidos en común con el organizador
        int partidosConOrganizador = contarPartidosEnComun(usuario, partido.getOrganizador());
        score += Math.min(30.0, partidosConOrganizador * 10.0); // Hasta 30 puntos
        
        // Contar partidos en común con jugadores actuales
        int partidosConJugadores = 0;
        for (Usuario jugador : partido.getJugadores()) {
            partidosConJugadores += contarPartidosEnComun(usuario, jugador);
        }
        
        if (!partido.getJugadores().isEmpty()) {
            double promedioPartidosComun = (double) partidosConJugadores / partido.getJugadores().size();
            score += Math.min(30.0, promedioPartidosComun * 10.0); // Hasta 30 puntos
        }
        
        // Bonus si ha jugado el mismo deporte antes
        if (usuario.getDeporteFavorito() == partido.getTipoDeporte()) {
            score += 10.0;
        }
        
        return Math.min(100.0, score);
    }
    
    @Override
    public String getNombre() {
        return "Historial de Partidos";
    }
    
    @Override
    public TipoEstrategia getTipo() {
        return TipoEstrategia.HISTORIAL;
    }
    
    /**
     * Cuenta partidos en común entre dos usuarios
     * En una implementación real, esto consultaría la base de datos
     */
    private int contarPartidosEnComun(Usuario usuario1, Usuario usuario2) {
        if (usuario1 == null || usuario2 == null) {
            return 0;
        }
        
        // Obtener partidos de ambos usuarios
        List<Partido> partidos1 = usuario1.getPartidos();
        List<Partido> partidos2 = usuario2.getPartidos();
        
        if (partidos1 == null || partidos2 == null) {
            return 0;
        }
        
        // Contar partidos en común
        int count = 0;
        for (Partido p1 : partidos1) {
            if (partidos2.stream().anyMatch(p2 -> p2.getId() != null && p2.getId().equals(p1.getId()))) {
                count++;
            }
        }
        
        return count;
    }
}
