package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
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
public class UsuarioResponseDTO {
    
    private Long id;
    private String nombreUsuario;
    private String email;
    private TipoDeporte deporteFavorito;
    private Usuario.NivelJuego nivelJuego;
    private String ubicacion;
    private boolean notificacionesEmail;
    private boolean notificacionesPush;
}
