package com.unomas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger
 */
@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Uno Mas - API de Gestión de Encuentros Deportivos")
                .version("1.0.0")
                .description("""
                    API REST para el sistema de gestión de encuentros deportivos.
                    
                    ## Patrones de Diseño Implementados:
                    - **MVC**: Arquitectura Model-View-Controller
                    - **Strategy**: Estrategias de emparejamiento de jugadores
                    - **State**: Estados del partido
                    - **Observer**: Sistema de notificaciones
                    - **Factory**: Creación de partidos
                    - **Adapter**: Adaptadores para servicios de notificación
                    
                    ## Funcionalidades:
                    - Registro y gestión de usuarios
                    - Creación y búsqueda de partidos
                    - Sistema de emparejamiento inteligente
                    - Notificaciones por email y push
                    - Gestión de estados del partido
                    """)
                .contact(new Contact()
                    .name("Equipo Uno Mas")
                    .email("info@unomas.com"))
                .license(new License()
                    .name("Proyecto Académico - ADOO")
                    .url("https://github.com/unomas")));
    }
}
