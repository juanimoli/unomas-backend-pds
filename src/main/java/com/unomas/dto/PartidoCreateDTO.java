package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear un partido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartidoCreateDTO {
    
    @NotNull(message = "El tipo de deporte es obligatorio")
    private TipoDeporte tipoDeporte;
    
    @Min(value = 2, message = "Se requieren al menos 2 jugadores")
    private Integer cantidadJugadoresRequeridos;
    
    @Min(value = 30, message = "La duración mínima es de 30 minutos")
    private Integer duracionMinutos;
    
    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion; // "latitud,longitud"
    
    private String direccion; // Dirección legible
    
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime fechaHora;
    
    @NotNull(message = "El ID del organizador es obligatorio")
    private Long organizadorId;
    
    private Usuario.NivelJuego nivelMinimoRequerido;
    private Usuario.NivelJuego nivelMaximoRequerido;
    private Boolean permiteCualquierNivel;
    private String descripcion;
}
