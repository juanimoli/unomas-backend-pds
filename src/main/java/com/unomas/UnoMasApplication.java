package com.unomas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Uno Mas
 * Sistema de gestión de encuentros deportivos
 */
@SpringBootApplication
@EnableScheduling
public class UnoMasApplication {

    private static final Logger logger = LoggerFactory.getLogger(UnoMasApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(UnoMasApplication.class, args);
        logger.info("===========================================");
        logger.info("Uno Mas Backend - Sistema de Encuentros Deportivos");
        logger.info("Aplicación iniciada correctamente");
        logger.info("Swagger UI: http://localhost:8080/swagger-ui.html");
        logger.info("===========================================");
    }
}
