package com.unomas.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración JPA
 * Asegura que las entidades y repositorios sean escaneados correctamente
 */
@Configuration
@EntityScan(basePackages = "com.unomas.model")
@EnableJpaRepositories(basePackages = "com.unomas.repository")
public class JpaConfig {
    // Esta clase configura el escaneo de entidades y repositorios
}
