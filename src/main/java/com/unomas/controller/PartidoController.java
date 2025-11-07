package com.unomas.controller;

import com.unomas.dto.PartidoBusquedaDTO;
import com.unomas.dto.PartidoCreateDTO;
import com.unomas.dto.PartidoResponseDTO;
import com.unomas.service.PartidoService;
import com.unomas.strategy.emparejamiento.TipoEstrategia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de partidos
 * Patrón MVC: Controller
 */
@RestController
@RequestMapping("/api/partidos")
@Tag(name = "Partidos", description = "API para gestión de partidos deportivos")
public class PartidoController {
    
    @Autowired
    private PartidoService partidoService;
    
    @PostMapping
    @Operation(summary = "Crear nuevo partido", description = "Crea un nuevo partido deportivo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partido creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario organizador no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> crearPartido(@Valid @RequestBody PartidoCreateDTO dto) {
        PartidoResponseDTO partido = partidoService.crearPartido(dto);
        return new ResponseEntity<>(partido, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Buscar partidos", description = "Busca partidos según diferentes criterios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de partidos encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado (si se proporciona usuarioId)",
            content = @Content)
    })
    public ResponseEntity<List<PartidoResponseDTO>> buscarPartidos(
            @RequestParam(required = false) String tipoDeporte,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) TipoEstrategia estrategiaEmparejamiento,
            @RequestParam(required = false) Long usuarioId) {
        
        PartidoBusquedaDTO busqueda = PartidoBusquedaDTO.builder()
                .tipoDeporte(tipoDeporte)
                .estado(estado)
                .estrategiaEmparejamiento(estrategiaEmparejamiento)
                .usuarioId(usuarioId)
                .build();
        
        List<PartidoResponseDTO> partidos = partidoService.buscarPartidos(busqueda);
        return ResponseEntity.ok(partidos);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener partido por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> obtenerPartido(@PathVariable Long id) {
        PartidoResponseDTO partido = partidoService.obtenerPartido(id);
        return ResponseEntity.ok(partido);
    }
    
    @PostMapping("/{id}/unirse")
    @Operation(summary = "Unirse a un partido", description = "Permite a un usuario unirse a un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario unido exitosamente al partido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede unir al partido (estado incorrecto, usuario ya inscrito, etc.)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido o usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> unirseAPartido(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        
        Long usuarioId = body.get("usuarioId");
        PartidoResponseDTO partido = partidoService.unirseAPartido(id, usuarioId);
        return ResponseEntity.ok(partido);
    }
    
    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar partido", description = "Confirma un partido (transición de estado)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido confirmado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede confirmar el partido (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> confirmarPartido(@PathVariable Long id) {
        PartidoResponseDTO partido = partidoService.confirmarPartido(id);
        return ResponseEntity.ok(partido);
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar partido", description = "Cancela un partido antes de su inicio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido cancelado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede cancelar el partido (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> cancelarPartido(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        String motivo = body.getOrDefault("motivo", "Sin motivo especificado");
        PartidoResponseDTO partido = partidoService.cancelarPartido(id, motivo);
        return ResponseEntity.ok(partido);
    }
    
    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar partido", description = "Inicia un partido (transición de estado)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido iniciado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede iniciar el partido (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> iniciarPartido(@PathVariable Long id) {
        PartidoResponseDTO partido = partidoService.iniciarPartido(id);
        return ResponseEntity.ok(partido);
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar partido", description = "Finaliza un partido en juego")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido finalizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede finalizar el partido (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> finalizarPartido(@PathVariable Long id) {
        PartidoResponseDTO partido = partidoService.finalizarPartido(id);
        return ResponseEntity.ok(partido);
    }
}
