# Guía de Uso - API Uno Mas

## Inicio Rápido

### 1. Iniciar la aplicación
```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en: http://localhost:8080

### 2. Acceder a Swagger UI
Abrir en el navegador: http://localhost:8080/swagger-ui.html

### 3. Consola H2 (Base de datos)
http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:unomas
- Usuario: sa
- Password: (dejar vacío)

---

## Ejemplos de Uso con cURL

### Registrar un Usuario

```bash
curl -X POST http://localhost:8080/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "juan_futbolero",
    "email": "juan@example.com",
    "contrasena": "password123",
    "deporteFavorito": "FUTBOL",
    "nivelJuego": "INTERMEDIO",
    "ubicacion": "-34.6037,-58.3816",
    "notificacionesEmail": true,
    "notificacionesPush": true
  }'
```

### Crear un Partido

```bash
curl -X POST http://localhost:8080/api/partidos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDeporte": "FUTBOL",
    "cantidadJugadoresRequeridos": 10,
    "duracionMinutos": 90,
    "ubicacion": "-34.6037,-58.3816",
    "direccion": "Parque Centenario, Buenos Aires",
    "fechaHora": "2025-11-05T18:00:00",
    "organizadorId": 1,
    "permiteCualquierNivel": true,
    "descripcion": "Partido amistoso de fútbol 5"
  }'
```

### Buscar Partidos Disponibles

```bash
# Buscar todos los partidos disponibles
curl http://localhost:8080/api/partidos

# Buscar partidos de fútbol
curl "http://localhost:8080/api/partidos?tipoDeporte=FUTBOL"

# Buscar con estrategia de emparejamiento por nivel
curl "http://localhost:8080/api/partidos?estrategiaEmparejamiento=NIVEL&usuarioId=1"

# Buscar con estrategia de cercanía
curl "http://localhost:8080/api/partidos?estrategiaEmparejamiento=CERCANIA&usuarioId=1"
```

### Unirse a un Partido

```bash
curl -X POST http://localhost:8080/api/partidos/1/unirse \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2
  }'
```

### Confirmar un Partido

```bash
curl -X PUT http://localhost:8080/api/partidos/1/confirmar
```

### Iniciar un Partido

```bash
curl -X PUT http://localhost:8080/api/partidos/1/iniciar
```

### Finalizar un Partido

```bash
curl -X PUT http://localhost:8080/api/partidos/1/finalizar
```

### Cancelar un Partido

```bash
curl -X PUT http://localhost:8080/api/partidos/1/cancelar \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Lluvia intensa"
  }'
```

### Obtener Información de un Usuario

```bash
curl http://localhost:8080/api/usuarios/1
```

---

## Ejemplos de Uso con Postman

### Collection de Postman

Importar la siguiente colección JSON en Postman:

```json
{
  "info": {
    "name": "Uno Mas API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Registrar Usuario",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "http://localhost:8080/api/usuarios/registro",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nombreUsuario\": \"juan_futbolero\",\n  \"email\": \"juan@example.com\",\n  \"contrasena\": \"password123\",\n  \"deporteFavorito\": \"FUTBOL\",\n  \"nivelJuego\": \"INTERMEDIO\",\n  \"ubicacion\": \"-34.6037,-58.3816\"\n}"
        }
      }
    },
    {
      "name": "Crear Partido",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "http://localhost:8080/api/partidos",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"tipoDeporte\": \"FUTBOL\",\n  \"cantidadJugadoresRequeridos\": 10,\n  \"duracionMinutos\": 90,\n  \"ubicacion\": \"-34.6037,-58.3816\",\n  \"direccion\": \"Parque Centenario\",\n  \"fechaHora\": \"2025-11-05T18:00:00\",\n  \"organizadorId\": 1\n}"
        }
      }
    }
  ]
}
```

---

## Escenarios de Uso Completos

### Escenario 1: Crear y completar un partido

```bash
# 1. Registrar usuarios
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/usuarios/registro \
    -H "Content-Type: application/json" \
    -d "{
      \"nombreUsuario\": \"usuario$i\",
      \"email\": \"usuario$i@example.com\",
      \"contrasena\": \"pass123\",
      \"nivelJuego\": \"INTERMEDIO\",
      \"deporteFavorito\": \"FUTBOL\"
    }"
done

# 2. Crear partido (usuario 1 es organizador)
curl -X POST http://localhost:8080/api/partidos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoDeporte": "FUTBOL_5",
    "cantidadJugadoresRequeridos": 5,
    "fechaHora": "2025-11-05T19:00:00",
    "ubicacion": "-34.6037,-58.3816",
    "organizadorId": 1
  }'

# 3. Usuarios se unen al partido
for i in {2..5}; do
  curl -X POST http://localhost:8080/api/partidos/1/unirse \
    -H "Content-Type: application/json" \
    -d "{\"usuarioId\": $i}"
done

# 4. Verificar estado (debería estar en PARTIDO_ARMADO)
curl http://localhost:8080/api/partidos/1

# 5. Confirmar el partido
curl -X PUT http://localhost:8080/api/partidos/1/confirmar

# 6. Iniciar el partido
curl -X PUT http://localhost:8080/api/partidos/1/iniciar

# 7. Finalizar el partido
curl -X PUT http://localhost:8080/api/partidos/1/finalizar
```

---

## Deportes Disponibles

- FUTBOL (11 jugadores)
- FUTBOL_5 (5 jugadores)
- FUTBOL_7 (7 jugadores)
- BASQUET (5 jugadores)
- VOLEY (6 jugadores)
- PADDLE (4 jugadores)
- TENIS (2 jugadores)
- RUGBY (15 jugadores)
- HOCKEY (11 jugadores)

## Niveles de Juego

- PRINCIPIANTE
- INTERMEDIO
- AVANZADO

## Estados del Partido

- NECESITAMOS_JUGADORES (inicial)
- PARTIDO_ARMADO (equipo completo)
- CONFIRMADO (todos confirmaron)
- EN_JUEGO (partido en curso)
- FINALIZADO (partido terminado)
- CANCELADO (partido cancelado)

## Estrategias de Emparejamiento

- **NIVEL**: Empareja por nivel de habilidad
- **CERCANIA**: Empareja por cercanía geográfica
- **HISTORIAL**: Empareja por historial de partidos previos

---

## Notas Importantes

1. **Notificaciones**: Por defecto, las notificaciones se simulan en los logs. Para activar email real, configurar `application.properties` con credenciales SMTP.

2. **Firebase**: Para notificaciones push reales, colocar el archivo `firebase-service-account.json` en `src/main/resources/` y activar con `firebase.enabled=true`.

3. **Base de Datos**: Se usa H2 en memoria. Los datos se pierden al reiniciar. Para persistencia, cambiar a PostgreSQL o MySQL.

4. **Inicio Automático**: Los partidos confirmados se inician automáticamente cuando llega su fecha/hora programada.
