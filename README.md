# Uno Mas - Sistema de Gestión de Encuentros Deportivos

Sistema backend REST API para gestión de partidos deportivos con emparejamiento inteligente de jugadores.

**Materia**: Análisis y Diseño Orientado a Objetos  
**Tecnología**: Java 17 + Spring Boot 3.2.0  
**Patrones**: 6 patrones de diseño implementados

## 🚀 Inicio Rápido

```bash
# Verificar requisitos
./check-requirements.sh

# Iniciar backend
./quick-start.sh

# O usar menú interactivo
./manage.sh
```

**URLs**:
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## 📋 Patrones de Diseño

### 1. MVC (Model-View-Controller)
**Model**: `Usuario`, `Partido` | **Controller**: REST endpoints | **Service**: Lógica de negocio

### 2. Strategy
Algoritmos de emparejamiento intercambiables: `NivelHabilidadStrategy`, `CercaniaStrategy`, `HistorialStrategy`

### 3. State
Estados del partido: `NECESITAMOS_JUGADORES` → `PARTIDO_ARMADO` → `CONFIRMADO` → `EN_JUEGO` → `FINALIZADO`

### 4. Observer
Sistema de notificaciones: `EmailNotificationObserver`, `PushNotificationObserver`

### 5. Factory
Creación de partidos: `PartidoFactory`

### 6. Adapter
Adaptadores de notificaciones: `EmailServiceAdapter`, `FirebaseServiceAdapter`

## 🎯 Endpoints Principales

**Usuarios**
- `POST /api/usuarios/registro` - Registrar usuario
- `GET /api/usuarios` - Listar usuarios

**Partidos**
- `POST /api/partidos` - Crear partido
- `GET /api/partidos` - Buscar partidos
- `POST /api/partidos/{id}/unirse` - Unirse
- `PUT /api/partidos/{id}/confirmar` - Confirmar
- `PUT /api/partidos/{id}/iniciar` - Iniciar
- `PUT /api/partidos/{id}/finalizar` - Finalizar

## 📁 Estructura

```
src/main/java/com/unomas/
├── adapter/          # Adapter Pattern
├── controller/       # REST API (MVC)
├── service/          # Lógica de negocio (MVC)
├── model/            # Entidades JPA (MVC)
├── state/            # State Pattern
├── strategy/         # Strategy Pattern
├── observer/         # Observer Pattern
├── factory/          # Factory Pattern
└── dto/              # Data Transfer Objects
```

## 🛠️ Tecnologías

Java 17 • Spring Boot 3.2.0 • Spring Data JPA • H2 Database • Lombok • SpringDoc OpenAPI

## 📚 Documentación

- `PATRONES.md` - Detalles de cada patrón
- `SCRIPTS_README.md` - Guía de scripts
- `TEST_API_MANUAL.md` - Pruebas manuales
