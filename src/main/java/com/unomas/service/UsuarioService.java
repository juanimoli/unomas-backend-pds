package com.unomas.service;

import com.unomas.dto.UsuarioRegistroDTO;
import com.unomas.dto.UsuarioResponseDTO;
import com.unomas.dto.PushTokenUpdateDTO;
import com.unomas.exception.ResourceNotFoundException;
import com.unomas.model.Ubicacion;
import com.unomas.model.Usuario;
import com.unomas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios
 * Patrón MVC: Service Layer
 */
@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Registra un nuevo usuario
     */
    public UsuarioResponseDTO registrarUsuario(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByNombreUsuario(dto.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        Ubicacion ubicacion = (dto.getLongitud() != null && dto.getLatitud() != null) 
            ? new Ubicacion(dto.getLongitud(), dto.getLatitud()) 
            : null;
        
        Usuario usuario = Usuario.builder()
                .nombreUsuario(dto.getNombreUsuario())
                .email(dto.getEmail())
                .contrasena(dto.getContrasena())
                .deporteFavorito(dto.getDeporteFavorito())
                .nivelJuego(dto.getNivelJuego())
                .ubicacion(ubicacion)
                .firebaseToken(dto.getFirebaseToken())
                .notificacionesEmail(dto.isNotificacionesEmail())
                .notificacionesPush(dto.isNotificacionesPush())
                .build();
        
        return mapearADTO(usuarioRepository.save(usuario));
    }
    
    /**
     * Obtiene un usuario por ID
     */
    public UsuarioResponseDTO obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapearADTO(usuario);
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Actualiza un usuario
     */
    public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRegistroDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        if (dto.getDeporteFavorito() != null) {
            usuario.setDeporteFavorito(dto.getDeporteFavorito());
        }
        if (dto.getNivelJuego() != null) {
            usuario.setNivelJuego(dto.getNivelJuego());
        }
        if (dto.getLongitud() != null && dto.getLatitud() != null) {
            usuario.setUbicacion(new Ubicacion(dto.getLongitud(), dto.getLatitud()));
        }
        if (dto.getFirebaseToken() != null) {
            usuario.setFirebaseToken(dto.getFirebaseToken());
        }
        
        usuario.setNotificacionesEmail(dto.isNotificacionesEmail());
        usuario.setNotificacionesPush(dto.isNotificacionesPush());
        
        return mapearADTO(usuarioRepository.save(usuario));
    }
    
    /**
     * Actualiza el token de notificaciones push de un usuario
     */
    public UsuarioResponseDTO actualizarPushToken(Long id, PushTokenUpdateDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        
        usuario.setFirebaseToken(dto.getPushToken());
        usuario.setNotificacionesPush(true);
        
        return mapearADTO(usuarioRepository.save(usuario));
    }
    
    /**
     * Obtiene entidad Usuario (uso interno)
     */
    public Usuario obtenerUsuarioEntity(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Guarda un usuario en la base de datos (uso interno y servicios relacionados)
     */
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Mapea Usuario a DTO
     */
    private UsuarioResponseDTO mapearADTO(Usuario usuario) {
        UsuarioResponseDTO.UsuarioResponseDTOBuilder builder = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombreUsuario(usuario.getNombreUsuario())
                .email(usuario.getEmail())
                .deporteFavorito(usuario.getDeporteFavorito())
                .nivelJuego(usuario.getNivelJuego())
                .notificacionesEmail(usuario.isNotificacionesEmail())
                .notificacionesPush(usuario.isNotificacionesPush());
        
        // Extraer coordenadas si hay ubicación
        if (usuario.getUbicacion() != null) {
            builder.longitud(usuario.getUbicacion().getLongitud())
                   .latitud(usuario.getUbicacion().getLatitud());
        }
        
        return builder.build();
    }
}
