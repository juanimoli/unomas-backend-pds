package com.unomas.factory;

import com.unomas.model.Partido;
import com.unomas.model.TipoDeporte;
import com.unomas.model.Ubicacion;
import com.unomas.model.Usuario;
import com.unomas.state.BuscandoJugadoresState;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Factory para crear instancias de Partido
 * Patrón Factory
 */
@Component
public class PartidoFactory {
    
    /**
     * Crea un nuevo partido con configuración predeterminada
     */
    public Partido crearPartido(
            TipoDeporte tipoDeporte,
            Usuario organizador,
            LocalDateTime fechaHora,
            Ubicacion ubicacion,
            String direccion
    ) {
        Partido partido = Partido.builder()
                .tipoDeporte(tipoDeporte)
                .cantidadJugadoresRequeridos(tipoDeporte.getJugadoresDefault())
                .duracionMinutos(90) // Duración predeterminada
                .ubicacion(ubicacion)
                .direccion(direccion)
                .fechaHora(fechaHora)
                .organizador(organizador)
                .jugadores(new ArrayList<>())
                .permiteCualquierNivel(true)
                .fechaCreacion(LocalDateTime.now())
                .estadoActual("BUSCANDO_JUGADORES")
                .build();
        
        // Establecer el estado inicial
        partido.setEstado(new BuscandoJugadoresState());
        
        return partido;
    }
    
    /**
     * Crea un partido con configuración personalizada
     */
    public Partido crearPartidoPersonalizado(
            TipoDeporte tipoDeporte,
            Usuario organizador,
            LocalDateTime fechaHora,
            Ubicacion ubicacion,
            String direccion,
            int cantidadJugadores,
            int duracionMinutos,
            Usuario.NivelJuego nivelMinimo,
            Usuario.NivelJuego nivelMaximo,
            String descripcion
    ) {
        Partido partido = Partido.builder()
                .tipoDeporte(tipoDeporte)
                .cantidadJugadoresRequeridos(cantidadJugadores)
                .duracionMinutos(duracionMinutos)
                .ubicacion(ubicacion)
                .direccion(direccion)
                .fechaHora(fechaHora)
                .organizador(organizador)
                .jugadores(new ArrayList<>())
                .nivelMinimoRequerido(nivelMinimo)
                .nivelMaximoRequerido(nivelMaximo)
                .permiteCualquierNivel(nivelMinimo == null && nivelMaximo == null)
                .descripcion(descripcion)
                .fechaCreacion(LocalDateTime.now())
                .estadoActual("BUSCANDO_JUGADORES")
                .build();
        
        // Establecer el estado inicial
        partido.setEstado(new BuscandoJugadoresState());
        
        return partido;
    }
    
    /**
     * Crea un partido rápido (configuración más simple)
     */
    public Partido crearPartidoRapido(
            TipoDeporte tipoDeporte,
            Usuario organizador,
            LocalDateTime fechaHora,
            Ubicacion ubicacion
    ) {
        return crearPartido(tipoDeporte, organizador, fechaHora, ubicacion, null);
    }
}
