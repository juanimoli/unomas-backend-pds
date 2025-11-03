# 🚀 INICIO RÁPIDO - UNO MAS

## ⚡ 3 Pasos para Ejecutar

### 1️⃣ Instalar Herramientas (primera vez solamente)

```bash
# macOS
brew install openjdk@17 maven

# Verificar
java -version  # Debe mostrar Java 17+
mvn -version   # Debe mostrar Maven 3.6+
```

### 2️⃣ Compilar y Ejecutar

```bash
cd /Users/juanimoli/Development/uno-mas-tp-adoo

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### 3️⃣ Probar la API

Abrir en el navegador: **http://localhost:8080/swagger-ui.html**

---

## 📱 Prueba Rápida (5 minutos)

### Opción A: Usar Swagger UI
1. Abrir http://localhost:8080/swagger-ui.html
2. Expandir **POST /api/usuarios/registro**
3. Click en "Try it out"
4. Usar este JSON:
```json
{
  "nombreUsuario": "test_user",
  "email": "test@example.com",
  "contrasena": "password123",
  "deporteFavorito": "FUTBOL",
  "nivelJuego": "INTERMEDIO"
}
```
5. Click "Execute"
6. Ver respuesta con usuario creado

### Opción B: Usar Script Automatizado
```bash
# Ejecutar todas las pruebas
./test-api.sh
```

### Opción C: Usar cURL
```bash
# Registrar usuario
curl -X POST http://localhost:8080/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "juan",
    "email": "juan@example.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO"
  }'

# Crear partido
curl -X POST http://localhost:8080/api/partidos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDeporte": "FUTBOL_5",
    "cantidadJugadoresRequeridos": 5,
    "fechaHora": "2025-11-10T18:00:00",
    "ubicacion": "-34.6037,-58.3816",
    "organizadorId": 1
  }'

# Buscar partidos
curl http://localhost:8080/api/partidos
```

---

## 🎯 Endpoints Principales

```
✅ POST   /api/usuarios/registro    - Registrar usuario
✅ GET    /api/usuarios             - Listar usuarios
✅ POST   /api/partidos             - Crear partido
✅ GET    /api/partidos             - Buscar partidos
✅ POST   /api/partidos/{id}/unirse - Unirse a partido
✅ PUT    /api/partidos/{id}/confirmar - Confirmar
✅ PUT    /api/partidos/{id}/iniciar - Iniciar
✅ PUT    /api/partidos/{id}/finalizar - Finalizar
```

---

## 📊 URLs Útiles

| Servicio | URL |
|----------|-----|
| API Base | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs JSON | http://localhost:8080/v3/api-docs |
| H2 Console | http://localhost:8080/h2-console |

**H2 Console Login:**
- JDBC URL: `jdbc:h2:mem:unomas`
- User: `sa`
- Password: (vacío)

---

## 🔍 Ver Logs de Notificaciones

Las notificaciones se simulan por defecto. Ver en la consola:
```
=== SIMULACIÓN DE EMAIL ===
Para: user@example.com
Asunto: Actualización del Partido
...
```

---

## 📚 Más Información

- **Documentación completa**: Ver `README.md`
- **Patrones de diseño**: Ver `PATRONES.md`
- **Ejemplos detallados**: Ver `EJEMPLOS_USO.md`
- **Instalación**: Ver `INSTALACION.md`
- **Checklist**: Ver `CHECKLIST_FINAL.md`

---

## ⚠️ Problemas Comunes

**Puerto 8080 ocupado:**
```bash
# Cambiar puerto en src/main/resources/application.properties
server.port=8081
```

**Maven no encontrado:**
```bash
brew install maven
```

**Java no encontrado:**
```bash
brew install openjdk@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

---

## 🎉 ¡Listo!

El sistema **Uno Mas** está funcionando correctamente si ves:

```
Uno Mas Backend - Sistema de Encuentros Deportivos
Aplicación iniciada correctamente
Swagger UI: http://localhost:8080/swagger-ui.html
```

**¡Buena suerte con el proyecto!** 🚀

---

_Sistema de Gestión de Encuentros Deportivos - ADOO 2025_
