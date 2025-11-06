package com.unomas.controller;

import com.unomas.service.MatcherService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<String> unirseAPartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        try {
            logger.info("Request: Usuario {} intentando unirse a partido {}", usuarioId, partidoId);
            matcherService.unirseAPartido(usuarioId, partidoId);
            
            String mensaje = String.format("Usuario %d unido exitosamente al partido %d", 
                                          usuarioId, partidoId);
            logger.info("Success: {}", mensaje);
            return ResponseEntity.ok(mensaje);
            
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al unirse: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error al unir usuario a partido: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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
    public ResponseEntity<String> confirmarPartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        try {
            logger.info("Request: Usuario {} confirmando partido {}", usuarioId, partidoId);
            matcherService.confirmarPartido(usuarioId, partidoId);
            
            String mensaje = String.format("Usuario %d confirmado para el partido %d", 
                                          usuarioId, partidoId);
            logger.info("Success: {}", mensaje);
            return ResponseEntity.ok(mensaje);
            
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al confirmar: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error al confirmar usuario en partido: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
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
    public ResponseEntity<String> bajarseDePartido(
            @PathVariable int partidoId,
            @RequestParam int usuarioId) {
        
        try {
            logger.info("Request: Usuario {} bajándose del partido {}", usuarioId, partidoId);
            matcherService.bajarseDePartido(usuarioId, partidoId);
            
            String mensaje = String.format("Usuario %d removido exitosamente del partido %d", 
                                          usuarioId, partidoId);
            logger.info("Success: {}", mensaje);
            return ResponseEntity.ok(mensaje);
            
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al bajarse: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error al bajar usuario del partido: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
