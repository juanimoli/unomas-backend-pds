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
        // El usuario es compatible solo si ha jugado previamente con alguno de los jugadores inscritos
        // (incluyendo al organizador, que siempre está "inscrito" al partido)
        
        // Verificar si el usuario ha jugado con el organizador
        if (contarPartidosEnComun(usuario, partido.getOrganizador()) > 0) {
            return true;
        }
        
        // Verificar si el usuario ha jugado con alguno de los jugadores inscritos
        if (partido.getJugadores() != null && !partido.getJugadores().isEmpty()) {
            for (Usuario jugador : partido.getJugadores()) {
                if (contarPartidosEnComun(usuario, jugador) > 0) {
                    return true;
                }
            }
        }
        
        // Si no ha jugado con ninguno (ni organizador ni jugadores), no es compatible
        return false;
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
        if (usuario.getDeporteFavorito() != null && usuario.getDeporteFavorito().equals(partido.getTipoDeporte())) {
            score += 10.0;
        }
        
        return Math.min(100.0, score);
    }
    
    @Override
    public String getNombre() {
        return "Historial de Partidos";
    }
    
    @Override
    public String getTipo() {
        return TipoEstrategia.HISTORIAL;
    }
    
    /**
     * Cuenta partidos en común entre dos usuarios
     * Considera tanto si jugaron juntos como jugadores inscritos,
     * como también si uno fue organizador y el otro jugador
     */
    private int contarPartidosEnComun(Usuario usuario1, Usuario usuario2) {
        if (usuario1 == null || usuario2 == null) {
            return 0;
        }
        
        // Obtener partidos de ambos usuarios (donde están inscritos como jugadores)
        List<Partido> partidos1 = usuario1.getPartidos();
        List<Partido> partidos2 = usuario2.getPartidos();
        
        if (partidos1 == null) {
            partidos1 = new ArrayList<>();
        }
        if (partidos2 == null) {
            partidos2 = new ArrayList<>();
        }
        
        // Contar partidos donde ambos estuvieron inscritos como jugadores
        int count = 0;
        for (Partido p1 : partidos1) {
            if (partidos2.stream().anyMatch(p2 -> p2.getId() != null && p2.getId().equals(p1.getId()))) {
                count++;
            }
        }
        
        // También considerar partidos donde uno fue organizador y el otro jugador
        // Usuario1 jugó en partidos organizados por usuario2
        for (Partido p : partidos1) {
            if (p.getOrganizador() != null && p.getOrganizador().getId() != null 
                && p.getOrganizador().getId().equals(usuario2.getId())) {
                count++;
            }
        }
        
        // Usuario2 jugó en partidos organizados por usuario1
        for (Partido p : partidos2) {
            if (p.getOrganizador() != null && p.getOrganizador().getId() != null 
                && p.getOrganizador().getId().equals(usuario1.getId())) {
                count++;
            }
        }
        
        return count;
    }
}
