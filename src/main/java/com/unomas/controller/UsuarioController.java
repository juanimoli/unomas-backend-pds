package com.unomas.controller;

import com.unomas.dto.UsuarioRegistroDTO;
import com.unomas.dto.UsuarioResponseDTO;
import com.unomas.dto.PushTokenUpdateDTO;
import com.unomas.service.UsuarioService;
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

/**
 * Controlador REST para gestión de usuarios
 * Patrón MVC: Controller
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo usuario", 
               description = "Crea un nuevo usuario en el sistema. Incluir 'pushToken' (token FCM) en el body si se desea habilitar notificaciones push.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content)
    })
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.registrarUsuario(dto);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerUsuario(id);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)))
    })
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRegistroDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(usuario);
    }
    
    @PutMapping("/{id}/push-token")
    @Operation(summary = "Actualizar token de notificaciones push", 
               description = "Actualiza el token FCM de Firebase para recibir notificaciones push")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Push token actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content)
    })
    public ResponseEntity<UsuarioResponseDTO> actualizarPushToken(
            @PathVariable Long id,
            @Valid @RequestBody PushTokenUpdateDTO dto) {
        UsuarioResponseDTO usuario = usuarioService.actualizarPushToken(id, dto);
        return ResponseEntity.ok(usuario);
    }
}
