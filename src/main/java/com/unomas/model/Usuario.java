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

    @Column(name = "deporte_favorito")
    private String deporteFavorito; // String: FUTBOL, BASQUET, etc.

    @Column(nullable = false, name = "nivel_juego")
    private String nivelJuego; // String: PRINCIPIANTE, INTERMEDIO, AVANZADO

    @Embedded
    private Ubicacion ubicacion; // Objeto Ubicacion embebido

    @ManyToMany(mappedBy = "jugadores")
    @Builder.Default
    private List<Partido> partidos = new ArrayList<>();

    @Column(name = "firebase_token")
    private String firebaseToken; // Token para notificaciones push

    @Column(name = "notificaciones_email")
    @Builder.Default
    private boolean notificacionesEmail = true;

    @Column(name = "notificaciones_push")
    @Builder.Default
    private boolean notificacionesPush = true;

    /**
     * Constantes para niveles de juego
     */
    public static class NivelJuego {
        public static final String PRINCIPIANTE = "PRINCIPIANTE";
        public static final String INTERMEDIO = "INTERMEDIO";
        public static final String AVANZADO = "AVANZADO";
        
        private NivelJuego() {}
    }

    // Métodos de dominio según diagrama
    
    /**
     * Une al usuario a un partido específico.
     * Agrega el partido a la lista de partidos del usuario.
     */
    public void unirseAPartido(Partido partido) {
        if (partido != null && !this.partidos.contains(partido)) {
            this.partidos.add(partido);
        }
    }

    /**
     * Remueve al usuario de un partido específico.
     * Elimina el partido de la lista de partidos del usuario.
     */
    public void bajarseDePartido(Partido partido) {
        if (partido != null) {
            this.partidos.remove(partido);
        }
    }
}
