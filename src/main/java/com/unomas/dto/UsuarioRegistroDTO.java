package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
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
public class UsuarioRegistroDTO {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombreUsuario;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;
    
    private TipoDeporte deporteFavorito;
    
    @NotNull(message = "El nivel de juego es obligatorio")
    private Usuario.NivelJuego nivelJuego;
    
    private String ubicacion; // "latitud,longitud"
    
    private String firebaseToken;
    
    private boolean notificacionesEmail = true;
    
    private boolean notificacionesPush = true;
}
