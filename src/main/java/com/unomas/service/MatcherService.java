package com.unomas.service;

import com.unomas.exception.ResourceNotFoundException;
import com.unomas.model.Partido;
import com.unomas.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(MatcherService.class);

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
     * @throws ResourceNotFoundException si el usuario o partido no existe
     * @throws IllegalStateException si el partido está completo o el usuario ya está unido
     */
    public void unirseAPartido(int usuarioId, int partidoId) {
        logger.info("Procesando unión de usuario {} a partido {}", usuarioId, partidoId);
        
        // Obtener entidades
        Usuario usuario = usuarioService.obtenerUsuarioEntity((long) usuarioId);
        Partido partido = partidoService.obtenerPartidoEntity((long) partidoId);
        
        // Validaciones
        if (partido.estaCompleto()) {
            throw new IllegalStateException(
                String.format("El partido %d ya está completo (%d/%d jugadores)", 
                    partidoId, partido.getJugadores().size(), partido.getCantidadJugadoresRequeridos())
            );
        }
        
        if (partido.getJugadores().contains(usuario)) {
            throw new IllegalStateException(
                String.format("El usuario %d ya está unido al partido %d", usuarioId, partidoId)
            );
        }
        
        // Coordinar la unión bidireccional
        usuario.unirseAPartido(partido);
        
        // Reconfigurar observers ANTES de agregar jugador (para que reciban notificación de cambio de estado)
        // Agregar temporalmente el usuario a la lista para que se le configure el observer
        partido.getJugadores().add(usuario);
        partidoService.reconfigurarObservers(partido);
        partido.getJugadores().remove(usuario);
        
        // Ahora sí agregar jugador (esto puede disparar cambio de estado y notificaciones)
        partido.agregarJugador(usuario);
        
        // Persistir cambios (el @Transactional se encarga del flush)
        usuarioService.guardarUsuario(usuario);
        partidoService.guardarPartido(partido);
        
        logger.info("Usuario {} unido exitosamente a partido {}. Jugadores: {}/{}", 
                   usuarioId, partidoId, partido.getJugadores().size(), 
                   partido.getCantidadJugadoresRequeridos());
    }

    /**
     * Confirma un usuario para un partido (podría incluir lógica adicional de confirmación).
     * Por ahora, delega a unirseAPartido pero podría extenderse para manejar
     * estados de confirmación, notificaciones especiales, etc.
     * 
     * @param usuarioId ID del usuario que confirma
     * @param partidoId ID del partido a confirmar
     */
    public void confirmarPartido(int usuarioId, int partidoId) {
        logger.info("Confirmando usuario {} para partido {}", usuarioId, partidoId);
        
        // Por ahora, confirmar es equivalente a unirse
        // Esto podría extenderse para manejar lógica adicional de confirmación
        unirseAPartido(usuarioId, partidoId);
        
        logger.info("Usuario {} confirmado para partido {}", usuarioId, partidoId);
    }

    /**
     * Remueve a un usuario de un partido.
     * 
     * @param usuarioId ID del usuario que se baja
     * @param partidoId ID del partido del que se baja
     */
    public void bajarseDePartido(int usuarioId, int partidoId) {
        logger.info("Procesando baja de usuario {} del partido {}", usuarioId, partidoId);
        
        Usuario usuario = usuarioService.obtenerUsuarioEntity((long) usuarioId);
        Partido partido = partidoService.obtenerPartidoEntity((long) partidoId);
        
        if (!partido.getJugadores().contains(usuario)) {
            throw new IllegalStateException(
                String.format("El usuario %d no está unido al partido %d", usuarioId, partidoId)
            );
        }
        
        // Coordinar la remoción bidireccional
        usuario.bajarseDePartido(partido);
        partido.getJugadores().remove(usuario);
        
        // Persistir cambios
        usuarioService.guardarUsuario(usuario);
        partidoService.guardarPartido(partido);
        
        logger.info("Usuario {} removido exitosamente del partido {}. Jugadores: {}/{}", 
                   usuarioId, partidoId, partido.getJugadores().size(), 
                   partido.getCantidadJugadoresRequeridos());
    }
}
