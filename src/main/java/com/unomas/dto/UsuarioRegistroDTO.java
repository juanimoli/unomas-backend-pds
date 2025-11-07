package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registro de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar un nuevo usuario en el sistema")
public class UsuarioRegistroDTO {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Schema(description = "Nombre de usuario único", example = "juanperez", required = true)
    private String nombreUsuario;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Schema(description = "Dirección de email del usuario", example = "juan@example.com", required = true)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario (será hasheada)", example = "miPassword123", required = true)
    private String contrasena;
    
    @Schema(description = "Deporte favorito del usuario", example = "FUTBOL")
    private TipoDeporte deporteFavorito;
    
    @NotNull(message = "El nivel de juego es obligatorio")
    @Schema(description = "Nivel de habilidad del usuario", 
            example = "INTERMEDIO", 
            required = true,
            allowableValues = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO", "PROFESIONAL"})
    private Usuario.NivelJuego nivelJuego;
    
    @Schema(description = "Longitud de la ubicación del usuario (coordenada GPS)", example = "-58.3816")
    private Double longitud;
    
    @Schema(description = "Latitud de la ubicación del usuario (coordenada GPS)", example = "-34.6037")
    private Double latitud;
    
    @Schema(description = "Token de dispositivo para notificaciones push (Firebase)", example = "abc123def456")
    private String pushToken;
    
    @Builder.Default
    @Schema(description = "Indica si el usuario desea recibir notificaciones por email", example = "true", defaultValue = "true")
    private boolean notificacionesEmail = true;
    
    @Builder.Default
    @Schema(description = "Indica si el usuario desea recibir notificaciones push", example = "true", defaultValue = "true")
    private boolean notificacionesPush = true;
}
