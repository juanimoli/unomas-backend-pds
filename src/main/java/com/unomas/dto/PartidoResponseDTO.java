package com.unomas.dto;

import com.unomas.model.TipoDeporte;
import com.unomas.model.Usuario;
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
public class PartidoResponseDTO {
    
    private Long id;
    private TipoDeporte tipoDeporte;
    private int cantidadJugadoresRequeridos;
    private int duracionMinutos;
    private Double longitud;  // Coordenada geográfica
    private Double latitud;   // Coordenada geográfica
    private String direccion;
    private LocalDateTime fechaHora;
    private String estadoActual;
    private UsuarioResponseDTO organizador;
    private List<UsuarioResponseDTO> jugadores;
    private Usuario.NivelJuego nivelMinimoRequerido;
    private Usuario.NivelJuego nivelMaximoRequerido;
    private boolean permiteCualquierNivel;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private int jugadoresFaltantes;
    private boolean estaCompleto;
}
