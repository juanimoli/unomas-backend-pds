package com.unomas.service;

import com.unomas.adapter.EmailServiceAdapter;
import com.unomas.adapter.FirebaseServiceAdapter;
import com.unomas.dto.*;
import com.unomas.exception.ResourceNotFoundException;
import com.unomas.factory.EmparejamientoStrategyFactory;
import com.unomas.factory.PartidoFactory;
import com.unomas.model.Partido;
import com.unomas.model.TipoDeporte;
import com.unomas.model.Ubicacion;
import com.unomas.model.Usuario;
import com.unomas.observer.PartidoListener;
import com.unomas.repository.PartidoRepository;
import com.unomas.strategy.emparejamiento.EmparejamientoStrategy;
import com.unomas.strategy.emparejamiento.TipoEstrategia;
import com.unomas.strategy.notificacion.EmailNotificationStrategy;
import com.unomas.strategy.notificacion.PushNotificationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de partidos
 * Patrón MVC: Service Layer
 * Integra todos los patrones: Factory, Strategy, State, Observer, Adapter
 * 
 * Factory Pattern:
 * - PartidoFactory: crea instancias de Partido
 * - EmparejamientoStrategyFactory: crea estrategias de emparejamiento en runtime
 * 
 * Esto reduce el acoplamiento - el servicio no conoce las implementaciones
 * concretas de las estrategias, solo usa el factory para obtenerlas.
 */
@Service
@Transactional
public class PartidoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PartidoService.class);

    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PartidoFactory partidoFactory; // Factory Pattern
    
    @Autowired
    private EmparejamientoStrategyFactory strategyFactory; // Factory Pattern para Strategies
    
    @Autowired
    private EmailServiceAdapter emailAdapter; // Adapter Pattern
    
    @Autowired
    private FirebaseServiceAdapter firebaseAdapter; // Adapter Pattern
    
    /**
     * Crea un nuevo partido
     * Utiliza Factory Pattern para crear el partido
     * Utiliza Observer Pattern para notificaciones
     */
    public PartidoResponseDTO crearPartido(PartidoCreateDTO dto) {
        logger.info("Creando nuevo partido de {}", dto.getTipoDeporte());
        
        // Obtener el organizador
        Usuario organizador = usuarioService.obtenerUsuarioEntity(dto.getOrganizadorId());
        
        // Crear objeto Ubicacion desde coordenadas
        Ubicacion ubicacion = new Ubicacion(dto.getLongitud(), dto.getLatitud());
        
        // Usar Factory para crear el partido
        Partido partido;
        if (dto.getCantidadJugadoresRequeridos() != null || 
            dto.getNivelMinimoRequerido() != null || 
            dto.getNivelMaximoRequerido() != null) {
            
            // Partido personalizado
            partido = partidoFactory.crearPartidoPersonalizado(
                dto.getTipoDeporte(),
                organizador,
                dto.getFechaHora(),
                ubicacion,
                dto.getDireccion(),
                dto.getCantidadJugadoresRequeridos() != null ? 
                    dto.getCantidadJugadoresRequeridos() : 
                    dto.getTipoDeporte().getJugadoresDefault(),
                dto.getDuracionMinutos() != null ? dto.getDuracionMinutos() : 90,
                dto.getNivelMinimoRequerido(),
                dto.getNivelMaximoRequerido(),
                dto.getDescripcion()
            );
        } else {
            // Partido con configuración predeterminada
            partido = partidoFactory.crearPartido(
                dto.getTipoDeporte(),
                organizador,
                dto.getFechaHora(),
                ubicacion,
                dto.getDireccion()
            );
        }
        
        // Guardar el partido
        partido = partidoRepository.save(partido);
        
        // Configurar observers (Patrón Observer)
        configurarObservers(partido);
        
        logger.info("Partido creado exitosamente con ID: {}", partido.getId());
        
        // Notificar a usuarios con el mismo deporte favorito
        notificarNuevoPartido(partido);
        
        return mapearADTO(partido);
    }
    
    /**
     * Busca partidos disponibles
     * Utiliza Strategy Pattern para filtrar según diferentes criterios
     */
    public List<PartidoResponseDTO> buscarPartidos(PartidoBusquedaDTO busqueda) {
        logger.info("Buscando partidos con criterios: {}", busqueda);
        
        List<Partido> partidos = partidoRepository.findAll();
        
        // Filtrar por estado si se especifica
        if (busqueda.getEstado() != null) {
            partidos = partidos.stream()
                .filter(p -> p.getEstadoActual().equals(busqueda.getEstado()))
                .collect(Collectors.toList());
        } else {
            // Por defecto, solo partidos que necesitan jugadores
            partidos = partidos.stream()
                .filter(p -> p.getEstadoActual().equals("NECESITAMOS_JUGADORES") || 
                           p.getEstadoActual().equals("PARTIDO_ARMADO"))
                .collect(Collectors.toList());
        }
        
        // Filtrar por deporte si se especifica
        if (busqueda.getTipoDeporte() != null) {
            TipoDeporte deporte = TipoDeporte.valueOf(busqueda.getTipoDeporte());
            partidos = partidos.stream()
                .filter(p -> p.getTipoDeporte() == deporte)
                .collect(Collectors.toList());
        }
        
        // Aplicar estrategia de emparejamiento si se especifica usuario
        if (busqueda.getUsuarioId() != null && busqueda.getEstrategiaEmparejamiento() != null) {
            Usuario usuario = usuarioService.obtenerUsuarioEntity(busqueda.getUsuarioId());
            
            // Usar Factory para crear la estrategia en runtime
            EmparejamientoStrategy strategy = strategyFactory.crearEstrategia(busqueda.getEstrategiaEmparejamiento());
            
            // Filtrar solo partidos compatibles
            partidos = partidos.stream()
                .filter(p -> strategy.esCompatible(usuario, p))
                .sorted((p1, p2) -> Double.compare(
                    strategy.calcularCompatibilidad(usuario, p2),
                    strategy.calcularCompatibilidad(usuario, p1)
                ))
                .collect(Collectors.toList());
        }
        
        return partidos.stream()
            .map(this::mapearADTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un partido por ID
     */
    public PartidoResponseDTO obtenerPartido(Long id) {
        Partido partido = obtenerPartidoEntity(id);
        return mapearADTO(partido);
    }
    
    /**
     * Un usuario se une a un partido
     * Utiliza State Pattern para verificar que se puede unir
     * Utiliza Observer Pattern para notificar cambios
     */
    public PartidoResponseDTO unirseAPartido(Long partidoId, Long usuarioId) {
        logger.info("Usuario {} intentando unirse al partido {}", usuarioId, partidoId);
        
        Partido partido = obtenerPartidoEntity(partidoId);
        Usuario usuario = usuarioService.obtenerUsuarioEntity(usuarioId);
        
        // Verificar que el partido acepta jugadores
        if (!partido.getEstadoActual().equals("NECESITAMOS_JUGADORES")) {
            throw new IllegalStateException("El partido no está aceptando jugadores");
        }
        
        // Verificar que el usuario cumple los requisitos
        if (!partido.isPermiteCualquierNivel()) {
            // Usar Factory para obtener la estrategia de nivel de habilidad
            EmparejamientoStrategy nivelStrategy = strategyFactory.crearEstrategia(TipoEstrategia.NIVEL_HABILIDAD);
            
            if (!nivelStrategy.esCompatible(usuario, partido)) {
                throw new IllegalArgumentException("No cumples con los requisitos de nivel para este partido");
            }
        }
        
        // Agregar jugador (esto puede cambiar el estado automáticamente)
        partido.agregarJugador(usuario);
        
        // Reconfigurar observers para incluir al nuevo jugador
        configurarObservers(partido);
        
        partido = partidoRepository.save(partido);
        
        logger.info("Usuario {} se unió al partido {} exitosamente", usuarioId, partidoId);
        
        return mapearADTO(partido);
    }
    
    /**
     * Confirma un partido
     * Patrón State: Cambia el estado del partido
     */
    public PartidoResponseDTO confirmarPartido(Long partidoId) {
        logger.info("Confirmando partido {}", partidoId);
        
        Partido partido = obtenerPartidoEntity(partidoId);
        
        // Usar el patrón State para cambiar el estado
        partido.getEstado().confirmar(partido);
        
        partido = partidoRepository.save(partido);
        
        return mapearADTO(partido);
    }
    
    /**
     * Cancela un partido
     * Patrón State: Cambia el estado del partido
     */
    public PartidoResponseDTO cancelarPartido(Long partidoId, String motivo) {
        logger.info("Cancelando partido {} por: {}", partidoId, motivo);
        
        Partido partido = obtenerPartidoEntity(partidoId);
        
        partido.setMotivoCancelacion(motivo);
        partido.setFechaCancelacion(LocalDateTime.now());
        
        // Usar el patrón State para cambiar el estado
        partido.getEstado().cancelar(partido);
        
        partido = partidoRepository.save(partido);
        
        return mapearADTO(partido);
    }
    
    /**
     * Inicia un partido
     * Patrón State: Cambia el estado del partido
     */
    public PartidoResponseDTO iniciarPartido(Long partidoId) {
        logger.info("Iniciando partido {}", partidoId);
        
        Partido partido = obtenerPartidoEntity(partidoId);
        
        // Usar el patrón State para cambiar el estado
        partido.getEstado().iniciar(partido);
        
        partido = partidoRepository.save(partido);
        
        return mapearADTO(partido);
    }
    
    /**
     * Finaliza un partido
     * Patrón State: Cambia el estado del partido
     */
    public PartidoResponseDTO finalizarPartido(Long partidoId) {
        logger.info("Finalizando partido {}", partidoId);
        
        Partido partido = obtenerPartidoEntity(partidoId);
        
        // Usar el patrón State para cambiar el estado
        partido.getEstado().finalizar(partido);
        
        partido = partidoRepository.save(partido);
        
        return mapearADTO(partido);
    }
    
    /**
     * Configura los observers del partido (Patrón Observer + Strategy + Adapter)
     * Crea listeners para cada jugador, con estrategias de notificación según preferencias.
     */
    private void configurarObservers(Partido partido) {
        logger.debug("Configurando observers para partido {}", partido.getId());
        
        // Crear estrategias de notificación
        EmailNotificationStrategy emailStrategy = new EmailNotificationStrategy(emailAdapter);
        PushNotificationStrategy pushStrategy = new PushNotificationStrategy(firebaseAdapter);
        
        // Agregar listener para el organizador
        if (partido.getOrganizador() != null) {
            if (partido.getOrganizador().isNotificacionesEmail()) {
                partido.agregarObserver(new PartidoListener(partido.getOrganizador(), emailStrategy));
            }
            if (partido.getOrganizador().isNotificacionesPush()) {
                partido.agregarObserver(new PartidoListener(partido.getOrganizador(), pushStrategy));
            }
        }
        
        // Agregar listeners para cada jugador
        for (Usuario jugador : partido.getJugadores()) {
            if (jugador.isNotificacionesEmail()) {
                partido.agregarObserver(new PartidoListener(jugador, emailStrategy));
            }
            if (jugador.isNotificacionesPush()) {
                partido.agregarObserver(new PartidoListener(jugador, pushStrategy));
            }
        }
        
        logger.debug("Configurados {} observers para partido {}", 
                    partido.getJugadores().size() + 1, partido.getId());
    }
    
    /**
     * Reconfigura los observers después de que se agreguen nuevos jugadores.
     * Método público para uso desde otros servicios (ej. MatcherService).
     */
    public void reconfigurarObservers(Partido partido) {
        // Limpiar observers existentes y reconfigurar
        partido.getObservers().clear();
        configurarObservers(partido);
        logger.debug("Observers reconfigurados para partido {}", partido.getId());
    }
    
    /**
     * Notifica a usuarios interesados sobre un nuevo partido
     */
    private void notificarNuevoPartido(Partido partido) {
        // Esta funcionalidad se puede expandir para notificar a usuarios
        // con el deporte favorito coincidente
        logger.info("Notificando nuevo partido de {} creado", partido.getTipoDeporte());
    }
    
    /**
     * Obtiene un partido por ID (uso interno y servicios relacionados)
     */
    protected Partido obtenerPartidoEntity(Long id) {
        return partidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Partido no encontrado con ID: " + id));
    }
    
    /**
     * Guarda un partido en la base de datos
     */
    protected Partido guardarPartido(Partido partido) {
        return partidoRepository.save(partido);
    }
    
    /**
     * Mapea Partido a DTO
     */
    private PartidoResponseDTO mapearADTO(Partido partido) {
        PartidoResponseDTO.PartidoResponseDTOBuilder builder = PartidoResponseDTO.builder()
            .id(partido.getId())
            .tipoDeporte(partido.getTipoDeporte())
            .cantidadJugadoresRequeridos(partido.getCantidadJugadoresRequeridos())
            .duracionMinutos(partido.getDuracionMinutos())
            .direccion(partido.getDireccion())
            .fechaHora(partido.getFechaHora())
            .estadoActual(partido.getEstadoActual())
            .organizador(mapearUsuarioADTO(partido.getOrganizador()))
            .jugadores(partido.getJugadores().stream()
                .map(this::mapearUsuarioADTO)
                .collect(Collectors.toList()))
            .nivelMinimoRequerido(partido.getNivelMinimoRequerido())
            .nivelMaximoRequerido(partido.getNivelMaximoRequerido())
            .permiteCualquierNivel(partido.isPermiteCualquierNivel())
            .descripcion(partido.getDescripcion())
            .fechaCreacion(partido.getFechaCreacion())
            .jugadoresFaltantes(partido.getJugadoresFaltantes())
            .estaCompleto(partido.estaCompleto());
        
        // Extraer coordenadas si hay ubicación
        if (partido.getUbicacion() != null) {
            builder.longitud(partido.getUbicacion().getLongitud())
                   .latitud(partido.getUbicacion().getLatitud());
        }
        
        return builder.build();
    }
    
    /**
     * Mapea Usuario a DTO (simple)
     */
    private UsuarioResponseDTO mapearUsuarioADTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
            .id(usuario.getId())
            .nombreUsuario(usuario.getNombreUsuario())
            .email(usuario.getEmail())
            .nivelJuego(usuario.getNivelJuego())
            .deporteFavorito(usuario.getDeporteFavorito())
            .longitud(usuario.getUbicacion() != null ? usuario.getUbicacion().getLongitud() : null)
            .latitud(usuario.getUbicacion() != null ? usuario.getUbicacion().getLatitud() : null)
            .notificacionesEmail(usuario.isNotificacionesEmail())
            .notificacionesPush(usuario.isNotificacionesPush())
            .build();
    }
}
