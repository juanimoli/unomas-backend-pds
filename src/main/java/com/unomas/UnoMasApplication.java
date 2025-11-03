package com.unomas;

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

    public static void main(String[] args) {
        SpringApplication.run(UnoMasApplication.class, args);
        System.out.println("===========================================");
        System.out.println("Uno Mas Backend - Sistema de Encuentros Deportivos");
        System.out.println("Aplicación iniciada correctamente");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("===========================================");
    }
}
