package com.unomas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para búsqueda de partidos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartidoBusquedaDTO {
    
    private String tipoDeporte;
    private String estado;
    private String nivelMinimo;
    private String nivelMaximo;
    private String ubicacion;
    private Double radioKm;
    private String estrategiaEmparejamiento; // "NIVEL", "CERCANIA", "HISTORIAL"
    private Long usuarioId; // Para aplicar estrategias de emparejamiento
}
