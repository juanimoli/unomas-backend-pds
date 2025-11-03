package com.unomas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Usuario - Representa un usuario del sistema
 * Patrón MVC: Model
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombreUsuario;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private TipoDeporte deporteFavorito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelJuego nivelJuego;

    private String ubicacion; // Latitud,Longitud

    @ManyToMany(mappedBy = "jugadores")
    private List<Partido> partidos = new ArrayList<>();

    @Column(name = "firebase_token")
    private String firebaseToken; // Token para notificaciones push

    @Column(name = "notificaciones_email")
    private boolean notificacionesEmail = true;

    @Column(name = "notificaciones_push")
    private boolean notificacionesPush = true;

    public enum NivelJuego {
        PRINCIPIANTE,
        INTERMEDIO,
        AVANZADO
    }
}
