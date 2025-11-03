package com.unomas.service;

import com.unomas.model.Partido;
import com.unomas.repository.PartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio programado para iniciar partidos automáticamente
 * Verifica cada minuto si hay partidos confirmados que deban iniciarse
 */
@Service
public class PartidoSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(PartidoSchedulerService.class);
    
    @Autowired
    private PartidoRepository partidoRepository;
    
    /**
     * Verifica cada minuto si hay partidos confirmados cuya hora de inicio ha llegado
     */
    @Scheduled(fixedRate = 60000) // Cada 60 segundos
    @Transactional
    public void iniciarPartidosAutomaticamente() {
        LocalDateTime ahora = LocalDateTime.now();
        
        // Buscar partidos confirmados cuya fecha/hora ya pasó
        List<Partido> partidosParaIniciar = partidoRepository.findPartidosParaIniciar(ahora);
        
        for (Partido partido : partidosParaIniciar) {
            try {
                logger.info("Iniciando automáticamente partido {} a las {}", 
                    partido.getId(), ahora);
                
                // Cambiar estado a "EN_JUEGO"
                partido.getEstado().iniciar(partido);
                partidoRepository.save(partido);
                
                logger.info("Partido {} iniciado exitosamente", partido.getId());
                
            } catch (Exception e) {
                logger.error("Error al iniciar partido {}: {}", 
                    partido.getId(), e.getMessage());
            }
        }
    }
}
