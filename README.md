# Uno Mas - Sistema de Gestión de Encuentros Deportivos

## Descripción
Sistema backend desarrollado con Spring Boot para la gestión de encuentros deportivos donde los usuarios pueden encontrar jugadores para completar equipos.

## Patrones de Diseño Implementados

### 1. **MVC (Model-View-Controller)**
- **Model**: Entidades JPA en `com.unomas.model`
- **Controller**: REST Controllers en `com.unomas.controller`
- **Service**: Lógica de negocio en `com.unomas.service`

### 2. **Strategy Pattern**
- **Ubicación**: `com.unomas.strategy`
- **Propósito**: Implementa diferentes estrategias de emparejamiento de jugadores
- **Implementaciones**:
  - `NivelHabilidadStrategy`: Empareja por nivel de habilidad
  - `CercaniaStrategy`: Empareja por cercanía geográfica
  - `HistorialStrategy`: Empareja por historial de partidos previos

### 3. **State Pattern**
- **Ubicación**: `com.unomas.state`
- **Propósito**: Gestiona los diferentes estados de un partido
- **Estados**:
  - `NecesitamosJugadoresState`
  - `PartidoArmadoState`
  - `ConfirmadoState`
  - `EnJuegoState`
  - `FinalizadoState`
  - `CanceladoState`

### 4. **Observer Pattern**
- **Ubicación**: `com.unomas.observer`
- **Propósito**: Sistema de notificaciones para eventos del partido
- **Componentes**:
  - `PartidoObservable`: Subject que notifica cambios
  - `NotificacionObserver`: Interface para observers
  - `EmailNotificationObserver`: Observer para notificaciones por email
  - `PushNotificationObserver`: Observer para notificaciones push

### 5. **Factory Pattern**
- **Ubicación**: `com.unomas.factory`
- **Propósito**: Creación de objetos complejos
- **Factories**:
  - `PartidoFactory`: Crea instancias de partidos
  - `NotificacionFactory`: Crea diferentes tipos de notificaciones

### 6. **Adapter Pattern**
- **Ubicación**: `com.unomas.adapter`
- **Propósito**: Adapta diferentes servicios de notificación a una interfaz común
- **Adapters**:
  - `EmailServiceAdapter`: Adapta JavaMail
  - `FirebaseServiceAdapter`: Adapta Firebase Cloud Messaging

## Tecnologías Utilizadas
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (para desarrollo)
- JavaMail
- Firebase Admin SDK
- Lombok
- SpringDoc OpenAPI (Swagger)

## Estructura del Proyecto
```
src/main/java/com/unomas/
├── adapter/          # Adapter Pattern
├── config/           # Configuraciones
├── controller/       # REST Controllers (MVC)
├── dto/              # Data Transfer Objects
├── exception/        # Manejo de excepciones
├── factory/          # Factory Pattern
├── model/            # Entidades (MVC)
├── observer/         # Observer Pattern
├── repository/       # Repositorios JPA
├── service/          # Servicios (MVC)
├── state/            # State Pattern
└── strategy/         # Strategy Pattern
```

## Instalación y Ejecución

### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior

### Pasos
1. Clonar el repositorio
2. Configurar las propiedades en `application.properties`
3. Ejecutar:
```bash
mvn clean install
mvn spring-boot:run
```

## API Documentation
Una vez iniciada la aplicación, acceder a:
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

## Endpoints Principales

### Usuarios
- `POST /api/usuarios/registro` - Registrar nuevo usuario
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `PUT /api/usuarios/{id}` - Actualizar usuario

### Partidos
- `POST /api/partidos` - Crear nuevo partido
- `GET /api/partidos` - Buscar partidos disponibles
- `GET /api/partidos/{id}` - Obtener partido por ID
- `POST /api/partidos/{id}/unirse` - Unirse a un partido
- `PUT /api/partidos/{id}/confirmar` - Confirmar partido
- `PUT /api/partidos/{id}/cancelar` - Cancelar partido
- `PUT /api/partidos/{id}/iniciar` - Iniciar partido
- `PUT /api/partidos/{id}/finalizar` - Finalizar partido

## Configuración de Notificaciones

### Email (JavaMail)
Configurar en `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-password
```

### Firebase (Push Notifications)
Colocar el archivo `firebase-service-account.json` en `src/main/resources/`

## Autor
Trabajo Práctico - Análisis y Diseño Orientado a Objetos

## Licencia
Proyecto académico
