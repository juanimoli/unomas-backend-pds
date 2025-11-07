package com.unomas.service;

import com.unomas.exception.ResourceNotFoundException;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio especializado en operaciones de matching/emparejamiento de usuarios con partidos.
 * Separa la lógica de matching de la gestión general de partidos.
 * 
 * Patrón: Service Layer especializado
 * Responsabilidad: Coordinar la unión/confirmación de usuarios a partidos
 */
@Service
@Transactional
public class MatcherService {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PartidoService partidoService;

    /**
     * Une a un usuario a un partido específico.
     * Coordina las operaciones entre Usuario y Partido.
     * 
     * @param usuarioId ID del usuario que se une
     * @param partidoId ID del partido al que se une
     * @return El partido actualizado después de unir al usuario
     * @throws ResourceNotFoundException si el usuario o partido no existe
     * @throws IllegalStateException si el partido está completo o el usuario ya está unido
     */
    public Partido unirseAPartido(int usuarioId, int partidoId) {
        Usuario usuario = usuarioService.obtenerUsuarioEntity((long) usuarioId);
        Partido partido = partidoService.obtenerPartidoEntity((long) partidoId);
        
        if (partido.estaCompleto()) {
            throw new IllegalStateException(
                String.format("El partido %d ya está completo", partidoId)
            );
        }
        
        if (partido.getJugadores().contains(usuario)) {
            throw new IllegalStateException(
                String.format("El usuario %d ya está unido al partido %d", usuarioId, partidoId)
            );
        }
        
        usuario.unirseAPartido(partido);
        
        // Agregar jugador (puede disparar cambio de estado y notificaciones)
        partido.agregarJugador(usuario);
        
        // Reconfigurar observers después de agregar el jugador para incluirlo en las notificaciones
        partidoService.reconfigurarObservers(partido);
        
        usuarioService.guardarUsuario(usuario);
        return partidoService.guardarPartido(partido);
    }

    /**
     * Confirma un usuario para un partido.
     * 
     * @param usuarioId ID del usuario que confirma
     * @param partidoId ID del partido a confirmar
     * @return El partido actualizado después de confirmar al usuario
     */
    public Partido confirmarPartido(int usuarioId, int partidoId) {
        return unirseAPartido(usuarioId, partidoId);
    }

    /**
     * Remueve a un usuario de un partido.
     * 
     * @param usuarioId ID del usuario que se baja
     * @param partidoId ID del partido del que se baja
     * @return El partido actualizado después de remover al usuario
     * @throws IllegalStateException si el partido está confirmado, cancelado o el usuario no está en el partido
     */
    public Partido bajarseDePartido(int usuarioId, int partidoId) {
        Usuario usuario = usuarioService.obtenerUsuarioEntity((long) usuarioId);
        Partido partido = partidoService.obtenerPartidoEntity((long) partidoId);
        
        if (!partido.getJugadores().contains(usuario)) {
            throw new IllegalStateException(
                String.format("El usuario %d no está unido al partido %d", usuarioId, partidoId)
            );
        }
        
        // Validar que el partido esté en un estado donde se permita bajarse
        String estadoActual = partido.getEstadoActual();
        if ("CONFIRMADO".equals(estadoActual) || "EN_JUEGO".equals(estadoActual) || 
            "FINALIZADO".equals(estadoActual) || "CANCELADO".equals(estadoActual)) {
            throw new IllegalStateException(
                String.format("No se puede bajar de un partido en estado %s", estadoActual)
            );
        }
        
        // Remover jugador (puede cambiar el estado del partido)
        usuario.bajarseDePartido(partido);
        partido.removerJugador(usuario);
        
        // Reconfigurar observers después de remover el jugador
        partidoService.reconfigurarObservers(partido);
        
        // Guardar cambios
        partidoService.guardarPartido(partido);
        usuarioService.guardarUsuario(usuario);
        
        return partido;
    }
}
