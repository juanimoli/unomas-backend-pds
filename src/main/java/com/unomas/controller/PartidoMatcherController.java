package com.unomas.controller;

import com.unomas.dto.PartidoResponseDTO;
import com.unomas.model.Partido;
import com.unomas.service.MatcherService;
import com.unomas.service.PartidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operaciones de matching/emparejamiento.
 * Separa las operaciones de unir usuarios a partidos de la gestión general de partidos.
 * 
 * Patrón: MVC - Controller
 * Endpoints: POST /api/matcher/*
 */
@RestController
@RequestMapping("/api/matcher")
@Tag(name = "Matcher", description = "API para operaciones de matching y emparejamiento")
public class PartidoMatcherController {
    
    private static final Logger logger = LoggerFactory.getLogger(PartidoMatcherController.class);

    @Autowired
    private MatcherService matcherService;
    
    @Autowired
    private PartidoService partidoService;

    /**
     * Une a un usuario a un partido.
     * POST /api/matcher/unirse/{partidoId}
     * 
     * @param partidoId ID del partido
     * @param usuarioId ID del usuario (enviado en el body)
     * @return 200 OK si la unión fue exitosa, 400/404 en caso de error
     */
    @PostMapping("/unirse/{partidoId}")
    @Operation(summary = "Unirse a un partido", 
               description = "Permite a un usuario unirse a un partido específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario unido exitosamente al partido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede unir al partido (estado incorrecto, usuario ya inscrito, etc.)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido o usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> unirseAPartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        logger.info("Request: Usuario {} intentando unirse a partido {}", usuarioId, partidoId);
        Partido partido = matcherService.unirseAPartido(usuarioId, partidoId);
        PartidoResponseDTO response = partidoService.mapearADTO(partido);
        logger.info("Success: Usuario {} unido exitosamente al partido {}", usuarioId, partidoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Confirma un usuario para un partido.
     * POST /api/matcher/confirmar/{partidoId}
     * 
     * @param partidoId ID del partido
     * @param usuarioId ID del usuario (enviado en el body)
     * @return 200 OK si la confirmación fue exitosa, 400/404 en caso de error
     */
    @PostMapping("/confirmar/{partidoId}")
    @Operation(summary = "Confirmar participación en un partido", 
               description = "Confirma la participación de un usuario en un partido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario confirmado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede confirmar (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido o usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> confirmarPartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        logger.info("Request: Usuario {} confirmando partido {}", usuarioId, partidoId);
        Partido partido = matcherService.confirmarPartido(usuarioId, partidoId);
        PartidoResponseDTO response = partidoService.mapearADTO(partido);
        logger.info("Success: Usuario {} confirmado para el partido {}", usuarioId, partidoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Baja a un usuario de un partido.
     * DELETE /api/matcher/bajarse/{partidoId}
     * 
     * @param partidoId ID del partido
     * @param usuarioId ID del usuario (query parameter)
     * @return 200 OK si la baja fue exitosa, 400/404 en caso de error
     */
    @DeleteMapping("/bajarse/{partidoId}")
    @Operation(summary = "Bajarse de un partido", 
               description = "Permite a un usuario bajarse de un partido al que estaba unido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario removido exitosamente del partido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PartidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "No se puede bajar del partido (estado incorrecto)",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Partido o usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<PartidoResponseDTO> bajarseDePartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        logger.info("Request: Usuario {} bajándose del partido {}", usuarioId, partidoId);
        Partido partido = matcherService.bajarseDePartido(usuarioId, partidoId);
        PartidoResponseDTO response = partidoService.mapearADTO(partido);
        logger.info("Success: Usuario {} removido exitosamente del partido {}", usuarioId, partidoId);
        return ResponseEntity.ok(response);
    }
}
