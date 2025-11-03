package com.unomas.model;

import com.unomas.observer.PartidoObservable;
import com.unomas.state.EstadoPartido;
import com.unomas.state.NecesitamosJugadoresState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Partido - Representa un encuentro deportivo
 * Patrón MVC: Model
 * Patrón State: Mantiene referencia al estado actual
 * Patrón Observer: Implementa PartidoObservable
 */
@Entity
@Table(name = "partidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partido extends PartidoObservable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeporte tipoDeporte;

    @Column(nullable = false)
    private int cantidadJugadoresRequeridos;

    @Column(nullable = false)
    private int duracionMinutos;

    @Column(nullable = false)
    private String ubicacion;

    private String direccion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String estadoActual;

    @ManyToOne
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;

    @ManyToMany
    @JoinTable(
        name = "partido_jugadores",
        joinColumns = @JoinColumn(name = "partido_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> jugadores = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Usuario.NivelJuego nivelMinimoRequerido;

    @Enumerated(EnumType.STRING)
    private Usuario.NivelJuego nivelMaximoRequerido;

    private boolean permiteCualquierNivel = true;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    private String motivoCancelacion;

    // Estado actual del partido (Patrón State)
    @Transient
    private EstadoPartido estado;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estadoActual = "NECESITAMOS_JUGADORES";
        this.estado = new NecesitamosJugadoresState();
    }

    @PostLoad
    protected void onLoad() {
        // Restaurar el estado desde la base de datos
        this.estado = EstadoPartido.fromString(this.estadoActual);
    }

    /**
     * Cambia el estado del partido
     * Patrón State: Delega el comportamiento al estado
     */
    public void cambiarEstado(EstadoPartido nuevoEstado) {
        this.estado = nuevoEstado;
        this.estadoActual = nuevoEstado.getNombre();
        notificarObservadores("Estado cambiado a: " + nuevoEstado.getNombre());
    }

    /**
     * Agrega un jugador al partido
     */
    public void agregarJugador(Usuario usuario) {
        if (!jugadores.contains(usuario)) {
            jugadores.add(usuario);
            
            // Si se completó el equipo, cambiar estado
            if (jugadores.size() >= cantidadJugadoresRequeridos) {
                estado.equipoCompleto(this);
            }
        }
    }

    /**
     * Verifica si el partido está completo
     */
    public boolean estaCompleto() {
        return jugadores.size() >= cantidadJugadoresRequeridos;
    }

    /**
     * Obtiene la cantidad de jugadores faltantes
     */
    public int getJugadoresFaltantes() {
        return Math.max(0, cantidadJugadoresRequeridos - jugadores.size());
    }
}
