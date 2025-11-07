package com.unomas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el token de notificaciones push de un usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushTokenUpdateDTO {
    
    @NotBlank(message = "El token push es requerido")
    private String pushToken;
    
    private String deviceType; // "ios" o "android"
    
    private String deviceName; // Nombre del dispositivo (opcional)
}
