package com.unomas.model;

import com.unomas.observer.IObservable;
import com.unomas.observer.IListener;
import com.unomas.state.EstadoPartido;
import com.unomas.state.BuscandoJugadoresState;
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
 * Patrón Observer: Implementa IObservable
 */
@Entity
@Table(name = "partidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partido implements IObservable {

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

    @Embedded
    private Ubicacion ubicacion; // Objeto Ubicacion embebido

    private String direccion;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, name = "estado_actual")
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
    @Builder.Default
    private List<Usuario> jugadores = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Usuario.NivelJuego nivelMinimoRequerido;

    @Enumerated(EnumType.STRING)
    private Usuario.NivelJuego nivelMaximoRequerido;

    @Builder.Default
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

    // Lista de observadores (Patrón Observer)
    @Transient
    private final List<IListener> observers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.estadoActual = "BUSCANDO_JUGADORES";
        this.estado = new BuscandoJugadoresState();
    }

    @PostLoad
    public void onLoad() {
        // Restaurar el estado desde la base de datos
        this.estado = EstadoPartido.fromString(this.estadoActual);
    }

    // Implementación de IObservable

    @Override
    public void agregarObserver(IListener observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void eliminarObserver(IListener observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObservadores() {
        for (IListener observer : observers) {
            observer.notificar(this);
        }
    }

    /**
     * Cambia el estado del partido
     * Patrón State: Delega el comportamiento al estado
     */
    public void cambiarEstado(EstadoPartido nuevoEstado) {
        this.estado = nuevoEstado;
        this.estadoActual = nuevoEstado.getNombre();
        notificarObservadores();
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
     * Remueve un jugador del partido
     * Si el equipo deja de estar completo, vuelve a BUSCANDO_JUGADORES
     */
    public void removerJugador(Usuario usuario) {
        boolean estabaCompleto = estaCompleto();
        jugadores.remove(usuario);
        
        // Si el partido estaba en PARTIDO_ARMADO y ahora no está completo,
        // volver a BUSCANDO_JUGADORES
        if (estabaCompleto && !estaCompleto() && "PARTIDO_ARMADO".equals(getEstadoActual())) {
            this.estado = new BuscandoJugadoresState();
            setEstadoActual("BUSCANDO_JUGADORES");
            notificarObservadores();
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
    
    /**
     * Obtiene la lista de observers (para reconfiguración)
     */
    public List<IListener> getObservers() {
        return observers;
    }
}
