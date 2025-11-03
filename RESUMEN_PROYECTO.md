# RESUMEN DEL PROYECTO - UNO MAS
## Sistema de Gestión de Encuentros Deportivos

---

## DATOS DEL PROYECTO

**Materia**: Análisis y Diseño Orientado a Objetos (ADOO)  
**Proyecto**: Trabajo Práctico Obligatorio - Uno Mas  
**Tecnología**: Java 17 + Spring Boot 3.2.0  
**Arquitectura**: Backend REST API  

---

## DESCRIPCIÓN

Sistema backend para la gestión de encuentros deportivos donde los usuarios pueden:
- Registrarse y crear perfiles deportivos
- Crear partidos para diferentes deportes
- Buscar y unirse a partidos disponibles
- Recibir notificaciones de cambios de estado
- Utilizar emparejamiento inteligente de jugadores

---

## PATRONES DE DISEÑO IMPLEMENTADOS (6 patrones)

### 1. ✅ MVC (Model-View-Controller)
**Ubicación**: Toda la aplicación
- **Model**: `com.unomas.model` (Usuario, Partido, TipoDeporte)
- **Controller**: `com.unomas.controller` (UsuarioController, PartidoController)
- **Service**: `com.unomas.service` (UsuarioService, PartidoService)

**Justificación**: Separa responsabilidades y organiza la aplicación en capas lógicas.

### 2. ✅ Strategy
**Ubicación**: `com.unomas.strategy`
- `EmparejamientoStrategy` (interface)
- `NivelHabilidadStrategy` - Empareja por nivel
- `CercaniaStrategy` - Empareja por distancia
- `HistorialStrategy` - Empareja por historial

**Justificación**: Permite cambiar dinámicamente el algoritmo de emparejamiento sin modificar el código cliente.

### 3. ✅ State
**Ubicación**: `com.unomas.state`
- `EstadoPartido` (interface)
- `NecesitamosJugadoresState`
- `PartidoArmadoState`
- `ConfirmadoState`
- `EnJuegoState`
- `FinalizadoState`
- `CanceladoState`

**Justificación**: Gestiona las transiciones de estado del partido de forma robusta y mantenible.

### 4. ✅ Observer
**Ubicación**: `com.unomas.observer`
- `PartidoObservable` (subject)
- `NotificacionObserver` (interface)
- `EmailNotificationObserver`
- `PushNotificationObserver`

**Justificación**: Desacopla el partido de los mecanismos de notificación, permitiendo agregar nuevos observers fácilmente.

### 5. ✅ Factory
**Ubicación**: `com.unomas.factory`
- `PartidoFactory` - Crea instancias de partidos

**Justificación**: Centraliza y simplifica la creación de objetos complejos con diferentes configuraciones.

### 6. ✅ Adapter
**Ubicación**: `com.unomas.adapter`
- `NotificacionServiceAdapter` (interface)
- `EmailServiceAdapter` - Adapta JavaMail
- `FirebaseServiceAdapter` - Adapta Firebase

**Justificación**: Unifica diferentes servicios de notificación bajo una interfaz común, facilitando el cambio de implementación.

---

## REQUERIMIENTOS FUNCIONALES IMPLEMENTADOS

### ✅ 1. Registro de Usuarios
- Endpoint: `POST /api/usuarios/registro`
- Campos: nombre de usuario, email, contraseña, deporte favorito, nivel de juego
- Configuración de notificaciones

### ✅ 2. Búsqueda de Partidos
- Endpoint: `GET /api/partidos`
- Filtros: tipo de deporte, estado, ubicación
- Estrategias de emparejamiento

### ✅ 3. Creación de Partidos
- Endpoint: `POST /api/partidos`
- Atributos: deporte, jugadores requeridos, duración, ubicación, horario
- Estado inicial: "NECESITAMOS_JUGADORES"

### ✅ 4. Estados del Partido
- **Necesitamos jugadores** → **Partido armado** (automático al completar equipo)
- **Partido armado** → **Confirmado** (todos aceptan)
- **Confirmado** → **En juego** (automático por fecha/hora)
- **En juego** → **Finalizado**
- Cualquier estado → **Cancelado** (antes de iniciar)

### ✅ 5. Estrategia de Emparejamiento
- Niveles: Principiante, Intermedio, Avanzado
- Configuración de nivel mínimo/máximo por partido
- Algoritmos: Por nivel, por cercanía, por historial

### ✅ 6. Notificaciones
- **Email** (JavaMail con simulación si no está configurado)
- **Push** (Firebase con simulación si no está configurado)
- Eventos notificados:
  - Nuevo partido para deporte favorito
  - Partido armado (equipo completo)
  - Partido confirmado
  - Cambio a "En juego", "Finalizado" o "Cancelado"

---

## REQUERIMIENTOS NO FUNCIONALES CUMPLIDOS

✅ Patrón arquitectónico MVC  
✅ Seis patrones de diseño implementados (se pidieron 4 mínimo)  
✅ Código en Java  
✅ Documentación completa  
✅ API REST funcional  

---

## ESTRUCTURA DEL PROYECTO

```
src/main/java/com/unomas/
├── UnoMasApplication.java         # Clase principal
├── adapter/                       # Patrón Adapter
│   ├── NotificacionServiceAdapter.java
│   ├── EmailServiceAdapter.java
│   └── FirebaseServiceAdapter.java
├── config/                        # Configuraciones
│   ├── OpenAPIConfig.java
│   └── FirebaseConfig.java
├── controller/                    # Controladores REST (MVC)
│   ├── UsuarioController.java
│   └── PartidoController.java
├── dto/                          # Data Transfer Objects
│   ├── UsuarioRegistroDTO.java
│   ├── UsuarioResponseDTO.java
│   ├── PartidoCreateDTO.java
│   ├── PartidoResponseDTO.java
│   └── PartidoBusquedaDTO.java
├── exception/                    # Manejo de excepciones
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── factory/                      # Patrón Factory
│   └── PartidoFactory.java
├── model/                        # Entidades (MVC)
│   ├── Usuario.java
│   ├── Partido.java
│   └── TipoDeporte.java
├── observer/                     # Patrón Observer
│   ├── NotificacionObserver.java
│   ├── PartidoObservable.java
│   ├── EmailNotificationObserver.java
│   └── PushNotificationObserver.java
├── repository/                   # Repositorios JPA
│   ├── UsuarioRepository.java
│   └── PartidoRepository.java
├── service/                      # Servicios (MVC)
│   ├── UsuarioService.java
│   ├── PartidoService.java
│   └── PartidoSchedulerService.java
├── state/                        # Patrón State
│   ├── EstadoPartido.java
│   ├── NecesitamosJugadoresState.java
│   ├── PartidoArmadoState.java
│   ├── ConfirmadoState.java
│   ├── EnJuegoState.java
│   ├── FinalizadoState.java
│   └── CanceladoState.java
└── strategy/                     # Patrón Strategy
    ├── EmparejamientoStrategy.java
    ├── NivelHabilidadStrategy.java
    ├── CercaniaStrategy.java
    └── HistorialStrategy.java
```

---

## TECNOLOGÍAS UTILIZADAS

- **Java 17**: Lenguaje de programación
- **Spring Boot 3.2.0**: Framework backend
- **Spring Data JPA**: Persistencia de datos
- **H2 Database**: Base de datos en memoria (desarrollo)
- **JavaMail**: Envío de emails
- **Firebase Admin SDK**: Notificaciones push
- **Lombok**: Reducción de boilerplate
- **SpringDoc OpenAPI**: Documentación automática (Swagger)
- **Maven**: Gestión de dependencias

---

## ENDPOINTS PRINCIPALES

### Usuarios
- `POST /api/usuarios/registro` - Registrar usuario
- `GET /api/usuarios/{id}` - Obtener usuario
- `GET /api/usuarios` - Listar usuarios
- `PUT /api/usuarios/{id}` - Actualizar usuario

### Partidos
- `POST /api/partidos` - Crear partido
- `GET /api/partidos` - Buscar partidos
- `GET /api/partidos/{id}` - Obtener partido
- `POST /api/partidos/{id}/unirse` - Unirse a partido
- `PUT /api/partidos/{id}/confirmar` - Confirmar partido
- `PUT /api/partidos/{id}/iniciar` - Iniciar partido
- `PUT /api/partidos/{id}/finalizar` - Finalizar partido
- `PUT /api/partidos/{id}/cancelar` - Cancelar partido

---

## CÓMO EJECUTAR

1. **Prerequisitos**: Java 17, Maven 3.6+

2. **Compilar**:
```bash
mvn clean install
```

3. **Ejecutar**:
```bash
mvn spring-boot:run
```

4. **Acceder**:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

---

## ARCHIVOS DE DOCUMENTACIÓN

- `README.md` - Información general del proyecto
- `PATRONES.md` - Explicación detallada de cada patrón
- `EJEMPLOS_USO.md` - Ejemplos de uso de la API
- `RESUMEN_PROYECTO.md` - Este archivo

---

## CARACTERÍSTICAS DESTACADAS

1. **Arquitectura limpia** con separación de responsabilidades
2. **Código mantenible** gracias a los patrones de diseño
3. **Extensibilidad** fácil para nuevas funcionalidades
4. **Documentación automática** con Swagger
5. **Manejo robusto de errores** con handler global
6. **Validaciones** con Bean Validation
7. **Logging** completo para debugging
8. **Inicio automático** de partidos programado
9. **Sistema de notificaciones** desacoplado y flexible
10. **Testing** básico incluido

---

## PRINCIPIOS SOLID APLICADOS

- **S**ingle Responsibility: Cada clase tiene una responsabilidad única
- **O**pen/Closed: Extensible mediante estrategias y observers
- **L**iskov Substitution: Las estrategias son intercambiables
- **I**nterface Segregation: Interfaces específicas y cohesivas
- **D**ependency Inversion: Dependencia de abstracciones, no implementaciones

---

## CONCLUSIÓN

El proyecto **Uno Mas** implementa exitosamente un sistema backend completo para gestión de encuentros deportivos, cumpliendo con todos los requerimientos funcionales y no funcionales del trabajo práctico.

Se han aplicado **6 patrones de diseño** (superando los 4 mínimos requeridos) de forma práctica y justificada, demostrando comprensión profunda de los conceptos de diseño orientado a objetos.

El código es profesional, mantenible, extensible y está completamente documentado, listo para presentación y evaluación.
