package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
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
@Schema(description = "DTO para crear un nuevo partido deportivo")
public class PartidoCreateDTO {
    
    @NotNull(message = "El tipo de deporte es obligatorio")
    @Schema(description = "Tipo de deporte del partido", 
            example = "FUTBOL", 
            required = true,
            allowableValues = {"FUTBOL", "BASQUET", "TENIS", "VOLLEY", "RUGBY", "PADDLE", "HOCKEY", "HANDBALL"})
    private TipoDeporte tipoDeporte;
    
    @Min(value = 2, message = "Se requieren al menos 2 jugadores")
    @Schema(description = "Cantidad de jugadores requeridos para completar el partido", 
            example = "10", 
            minimum = "2",
            required = true)
    private Integer cantidadJugadoresRequeridos;
    
    @Min(value = 30, message = "La duración mínima es de 30 minutos")
    @Schema(description = "Duración estimada del partido en minutos", 
            example = "90", 
            minimum = "30",
            required = true)
    private Integer duracionMinutos;
    
    @NotNull(message = "La longitud es obligatoria")
    @Schema(description = "Longitud de la ubicación del partido (coordenada GPS)", 
            example = "-58.3816", 
            required = true)
    private Double longitud;
    
    @NotNull(message = "La latitud es obligatoria")
    @Schema(description = "Latitud de la ubicación del partido (coordenada GPS)", 
            example = "-34.6037", 
            required = true)
    private Double latitud;
    
    @Schema(description = "Dirección legible de la ubicación del partido", 
            example = "Av. Corrientes 1234, CABA")
    private String direccion; // Dirección legible
    
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La fecha debe ser futura")
    @Schema(description = "Fecha y hora programada para el partido", 
            example = "2025-12-25T18:00:00", 
            required = true)
    private LocalDateTime fechaHora;
    
    @NotNull(message = "El ID del organizador es obligatorio")
    @Schema(description = "ID del usuario que organiza el partido", 
            example = "1", 
            required = true)
    private Long organizadorId;
    
    @Schema(description = "Nivel mínimo de habilidad requerido para unirse al partido", 
            example = "INTERMEDIO",
            allowableValues = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO", "PROFESIONAL"})
    private Usuario.NivelJuego nivelMinimoRequerido;
    
    @Schema(description = "Nivel máximo de habilidad permitido para unirse al partido", 
            example = "AVANZADO",
            allowableValues = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO", "PROFESIONAL"})
    private Usuario.NivelJuego nivelMaximoRequerido;
    
    @Schema(description = "Si es true, permite jugadores de cualquier nivel (ignora restricciones de nivel)", 
            example = "false")
    private Boolean permiteCualquierNivel;
    
    @Schema(description = "Descripción adicional del partido (reglas, requisitos, etc.)", 
            example = "Partido amistoso, traer botella de agua")
    private String descripcion;
}
