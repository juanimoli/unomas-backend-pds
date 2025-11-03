package com.unomas.repository;

import com.unomas.model.Partido;
import com.unomas.model.TipoDeporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Partido
 */
@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    
    List<Partido> findByEstadoActual(String estadoActual);
    
    List<Partido> findByTipoDeporte(TipoDeporte tipoDeporte);
    
    List<Partido> findByOrganizadorId(Long organizadorId);
    
    @Query("SELECT p FROM Partido p WHERE p.tipoDeporte = :deporte AND p.estadoActual = :estado")
    List<Partido> findByDeporteYEstado(
        @Param("deporte") TipoDeporte deporte, 
        @Param("estado") String estado
    );
    
    @Query("SELECT p FROM Partido p WHERE p.fechaHora >= :fechaInicio AND p.fechaHora <= :fechaFin")
    List<Partido> findByRangoFechas(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    @Query("SELECT p FROM Partido p WHERE p.estadoActual = 'CONFIRMADO' AND p.fechaHora <= :fecha")
    List<Partido> findPartidosParaIniciar(@Param("fecha") LocalDateTime fecha);
}
