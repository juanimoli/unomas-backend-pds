# Uno Mas - Sistema de Gestión de Encuentros Deportivos

Sistema backend REST API + app Android nativa para gestión de partidos deportivos con emparejamiento inteligente de jugadores.

**Materia**: Análisis y Diseño Orientado a Objetos  
**Stack**: 
- **Backend**: Java 17 + Spring Boot 3.2.0
- **Mobile**: Android nativo + Firebase FCM
- **Patrones**: 6 patrones de diseño implementados

**URLs Backend**:
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

## 📱 App Android Nativa

La app móvil está en `android-app/`. Ver [`android-app/README.md`](android-app/README.md) para:
- Compilación e instalación
- Configuración de Firebase FCM
- Testing de notificaciones push

**Características**:
- ✅ 100% Android nativo (Java)
- ✅ Firebase Cloud Messaging integrado
- ✅ UI simple con logo y display de token FCM
- ✅ Build rápido (~2-3 min) y APK pequeño (~10MB)

## 🎯 Endpoints Principales

**Usuarios**
- `POST /api/usuarios/registro` - Registrar usuario (incluir `pushToken` para notificaciones)
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `GET /api/usuarios` - Listar todos los usuarios
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `PUT /api/usuarios/{id}/push-token` - Actualizar token FCM para notificaciones push

**Partidos**
- `POST /api/partidos` - Crear partido
- `GET /api/partidos` - Buscar partidos
- `POST /api/partidos/{id}/unirse` - Unirse a partido
- `PUT /api/partidos/{id}/confirmar` - Confirmar partido
- `PUT /api/partidos/{id}/iniciar` - Iniciar partido
- `PUT /api/partidos/{id}/finalizar` - Finalizar partido

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

**Backend**: Java 17 • Spring Boot 3.2.0 • Spring Data JPA • H2 Database • Lombok • SpringDoc OpenAPI • Firebase Admin SDK

**Mobile**: Android SDK 34 • Firebase FCM • Material Components • Gradle 8.1

## 🚀 Quick Start

### Backend
```bash
./mvnw clean package -DskipTests
java -jar target/unomas-backend-1.0.0.jar
```

### App Android
```bash
cd android-app
./build.sh
adb install app/build/outputs/apk/debug/app-debug.apk
```

Ver documentación completa en `android-app/README.md`

