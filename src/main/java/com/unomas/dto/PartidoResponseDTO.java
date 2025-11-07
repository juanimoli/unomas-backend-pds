package com.unomas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de partido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta que representa un partido deportivo completo")
public class PartidoResponseDTO {
    
    @Schema(description = "ID único del partido", example = "1")
    private Long id;
    
    @Schema(description = "Tipo de deporte del partido", example = "FUTBOL")
    private String tipoDeporte;
    
    @Schema(description = "Cantidad total de jugadores requeridos para el partido", example = "10")
    private int cantidadJugadoresRequeridos;
    
    @Schema(description = "Duración estimada del partido en minutos", example = "90")
    private int duracionMinutos;
    
    @Schema(description = "Longitud de la ubicación (coordenada GPS)", example = "-58.3816")
    private Double longitud;
    
    @Schema(description = "Latitud de la ubicación (coordenada GPS)", example = "-34.6037")
    private Double latitud;
    
    @Schema(description = "Dirección legible de la ubicación", example = "Av. Corrientes 1234, CABA")
    private String direccion;
    
    @Schema(description = "Fecha y hora programada del partido", example = "2025-12-25T18:00:00")
    private LocalDateTime fechaHora;
    
    @Schema(description = "Estado actual del partido", 
            example = "BUSCANDO_JUGADORES",
            allowableValues = {"BUSCANDO_JUGADORES", "PARTIDO_ARMADO", "CONFIRMADO", "EN_JUEGO", "FINALIZADO", "CANCELADO"})
    private String estadoActual;
    
    @Schema(description = "Usuario organizador del partido")
    private UsuarioResponseDTO organizador;
    
    @Schema(description = "Lista de jugadores inscritos en el partido")
    private List<UsuarioResponseDTO> jugadores;
    
    @Schema(description = "Nivel mínimo de habilidad requerido", example = "INTERMEDIO")
    private String nivelMinimoRequerido;
    
    @Schema(description = "Nivel máximo de habilidad permitido", example = "AVANZADO")
    private String nivelMaximoRequerido;
    
    @Schema(description = "Indica si permite jugadores de cualquier nivel", example = "false")
    private boolean permiteCualquierNivel;
    
    @Schema(description = "Descripción adicional del partido", example = "Partido amistoso, traer botella de agua")
    private String descripcion;
    
    @Schema(description = "Fecha y hora de creación del partido", example = "2025-11-01T10:00:00")
    private LocalDateTime fechaCreacion;
    
    @Schema(description = "Cantidad de jugadores que faltan para completar el equipo", example = "3")
    private int jugadoresFaltantes;
    
    @Schema(description = "Indica si el equipo está completo", example = "false")
    private boolean estaCompleto;
}
