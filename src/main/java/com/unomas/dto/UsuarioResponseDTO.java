package com.unomas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta que representa un usuario registrado")
public class UsuarioResponseDTO {
    
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Nombre de usuario", example = "juanperez")
    private String nombreUsuario;
    
    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String email;
    
    @Schema(description = "Deporte favorito del usuario", example = "FUTBOL")
    private String deporteFavorito;
    
    @Schema(description = "Nivel de habilidad del jugador", example = "INTERMEDIO")
    private String nivelJuego;
    
    @Schema(description = "Longitud de la ubicación del usuario (coordenada GPS)", example = "-58.3816")
    private Double longitud;
    
    @Schema(description = "Latitud de la ubicación del usuario (coordenada GPS)", example = "-34.6037")
    private Double latitud;
    
    @Schema(description = "Indica si el usuario recibe notificaciones por email", example = "true")
    private boolean notificacionesEmail;
    
    @Schema(description = "Indica si el usuario recibe notificaciones push", example = "true")
    private boolean notificacionesPush;
}
