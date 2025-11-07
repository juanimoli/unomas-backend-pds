# Uno Más - Aplicación Android Nativa

Aplicación móvil Android nativa para el sistema de encuentros deportivos "Uno Más".

## 🏗️ Arquitectura

- **Android nativo** (Java)
- **Firebase Cloud Messaging (FCM)** para notificaciones push
- **Material Design** para UI
- **Gradle** para build system

## 📋 Requisitos

- Android Studio Arctic Fox o superior
- Android SDK 24+ (Android 7.0 Nougat)
- Target SDK 34 (Android 14)
- JDK 8+

## 🚀 Configuración

### 1. Abrir el proyecto

```bash
cd android-app
# Abrir en Android Studio o usar línea de comandos:
./gradlew build
```

### 2. Archivo `google-services.json`

El archivo ya está incluido en `app/google-services.json` con las credenciales de Firebase:

```json
{
  "project_info": {
    "project_id": "unomas-tp-pds",
    "project_number": "848410014501"
  }
}
```

**⚠️ Importante**: Este archivo contiene credenciales sensibles. Está en `.gitignore` por seguridad.

### 3. Compilar la aplicación

#### Opción A: Android Studio
1. Abrir el proyecto en Android Studio
2. Esperar que Gradle sincronice las dependencias
3. Presionar el botón "Run" (▶️) o `Shift + F10`

#### Opción B: Línea de comandos
```bash
cd android-app

# Build de debug (con depuración)
./gradlew assembleDebug

# El APK se generará en: app/build/outputs/apk/debug/app-debug.apk

# Instalar en dispositivo conectado
./gradlew installDebug

# O instalar manualmente
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Opción C: Build de release (producción)
```bash
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release-unsigned.apk
```

## 📱 Ejecutar en Emulador

### Crear emulador (si no tienes uno)
```bash
# Listar AVDs disponibles
emulator -list-avds

# Si no hay ninguno, crear uno:
# En Android Studio: Tools > Device Manager > Create Device

# Iniciar emulador
emulator -avd Pixel_8_Pro_API_34
```

### Instalar y ejecutar
```bash
# Instalar
adb install app/build/outputs/apk/debug/app-debug.apk

# Abrir la aplicación
adb shell am start -n com.unomas.mobile/.MainActivity
```

## 🔔 Probar Notificaciones Push

### 1. Obtener el token FCM

Al abrir la app, verás el token FCM en pantalla. Ejemplo:
```
cZ8xKR3qQ... (token largo)
```

### 2. Registrar usuario con el token

```bash
curl -X POST http://localhost:8080/api/usuarios/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Usuario Android",
    "email": "android@test.com",
    "telefono": "1234567890",
    "nivelJuego": 5,
    "notificacionesEmail": false,
    "notificacionesPush": true,
    "pushToken": "TU_TOKEN_FCM_AQUI",
    "longitud": -58.3816,
    "latitud": -34.6037
  }'
```

### 3. Enviar notificación de prueba

```bash
curl -X POST "http://localhost:8080/api/notificaciones/test-push?userId=1"
```

Deberías ver la notificación en el dispositivo/emulador.

## 📁 Estructura del Proyecto

```
android-app/
├── app/
│   ├── build.gradle                          # Dependencias y configuración
│   ├── google-services.json                   # Credenciales Firebase
│   ├── proguard-rules.pro                     # Reglas ProGuard
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml            # Permisos y componentes
│           ├── java/com/unomas/mobile/
│           │   ├── MainActivity.java           # Activity principal
│           │   └── MyFirebaseMessagingService.java  # Servicio FCM
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml       # UI principal
│               ├── values/
│               │   ├── strings.xml             # Textos
│               │   └── themes.xml              # Temas Material
│               └── mipmap/                     # Iconos (generar)
├── build.gradle                               # Configuración root
├── settings.gradle                            # Módulos del proyecto
└── README.md                                  # Este archivo
```

## 🎨 Interfaz de Usuario

La aplicación muestra:
- **Logo**: Emojis de deportes ⚽🏀🎾
- **Título**: "Uno Más"
- **Subtítulo**: "Sistema de Encuentros Deportivos"
- **Estado**: Indica si el token se obtuvo correctamente
- **Token FCM**: Token completo y seleccionable
- **Hint**: Instrucción de uso del token

## 🔧 Componentes Clave

### MainActivity.java
- Solicita permisos de notificación (Android 13+)
- Obtiene el token FCM de Firebase
- Muestra el token en la UI

### MyFirebaseMessagingService.java
- Recibe notificaciones push de Firebase
- Crea canal de notificaciones
- Muestra notificaciones con título y cuerpo

### AndroidManifest.xml
- Permisos: `INTERNET`, `POST_NOTIFICATIONS`
- Registra el servicio FCM

## 🔐 Seguridad

**Archivos sensibles gitignoreados:**
- `app/google-services.json` (credenciales Firebase)
- `build/` (artefactos de compilación)
- `local.properties` (rutas locales)
- `.gradle/` (cache de Gradle)

## 🐛 Troubleshooting

### Error: "Firebase not initialized"
- Verificar que `google-services.json` está en `app/google-services.json`
- Verificar que el `package_name` en Firebase coincide con `com.unomas.mobile`
- Reconstruir el proyecto: `./gradlew clean build`

### Token no aparece
- Verificar que Google Play Services estén instalados en el emulador
- Verificar permisos de notificación otorgados
- Ver logs: `adb logcat | grep FCM`

### Notificaciones no llegan
- Verificar que Firebase está habilitado en el backend (`firebase.enabled=true`)
- Verificar que el `firebase-service-account.json` está en `src/main/resources/` del backend
- Ver logs del backend para errores de FCM

##  Recursos

- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/android/client)
- [Android Notifications Guide](https://developer.android.com/develop/ui/views/notifications)
- [Material Design Components](https://material.io/develop/android)

---

**Versión**: 1.0.0  
**Package**: com.unomas.mobile  
**Build Tools**: Gradle 8.1.0  
**Min SDK**: 24 (Android 7.0)  
**Target SDK**: 34 (Android 14)
