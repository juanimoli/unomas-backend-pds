#!/bin/bash

# Test simple de notificaciones
BASE_URL="http://localhost:8080/api"

echo "======================================"
echo "🧪 TEST SIMPLE DE NOTIFICACIONES"
echo "======================================"
echo ""

# Crear usuario con email
echo "📧 Creando usuario con notificaciones EMAIL..."
USER1=$(curl -s -X POST "${BASE_URL}/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "test_email",
    "email": "test@example.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": true,
    "notificacionesPush": false,
    "latitud": -34.60,
    "longitud": -58.38
  }')

echo "$USER1" | python3 -m json.tool
USER1_ID=$(echo "$USER1" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Usuario 1 ID: $USER1_ID"
echo ""

# Crear usuario con push
echo "🔔 Creando usuario con notificaciones PUSH..."
USER2=$(curl -s -X POST "${BASE_URL}/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "test_push",
    "email": "test2@example.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": false,
    "notificacionesPush": true,
    "firebaseToken": "test-firebase-token-123456",
    "latitud": -34.60,
    "longitud": -58.38
  }')

echo "$USER2" | python3 -m json.tool
USER2_ID=$(echo "$USER2" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Usuario 2 ID: $USER2_ID"
echo ""

# Crear partido
echo "⚽ Creando partido..."
PARTIDO=$(curl -s -X POST "${BASE_URL}/partidos" \
  -H "Content-Type: application/json" \
  -d "{
    \"organizadorId\": ${USER1_ID},
    \"tipoDeporte\": \"FUTBOL\",
    \"fechaHora\": \"2025-11-15T18:00:00\",
    \"direccion\": \"Cancha Test\",
    \"cantidadJugadoresRequeridos\": 10,
    \"nivelMinimoRequerido\": \"PRINCIPIANTE\",
    \"nivelMaximoRequerido\": \"AVANZADO\",
    \"duracionMinutos\": 90,
    \"descripcion\": \"Test de notificaciones\",
    \"latitud\": -34.60,
    \"longitud\": -58.38
  }")

echo "$PARTIDO" | python3 -m json.tool
PARTIDO_ID=$(echo "$PARTIDO" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Partido ID: $PARTIDO_ID"
echo ""

echo "======================================"
echo "📨 UNIENDO JUGADOR AL PARTIDO..."
echo "Esto debe generar notificación EMAIL al organizador"
echo "======================================"
curl -s -X POST "${BASE_URL}/partidos/${PARTIDO_ID}/unirse" \
  -H "Content-Type: application/json" \
  -d "{\"usuarioId\": ${USER2_ID}}" | python3 -m json.tool
echo ""

sleep 2

echo "======================================"
echo "✅ CONFIRMANDO PARTIDO..."
echo "Esto debe notificar a AMBOS usuarios según sus preferencias"
echo "======================================"
curl -s -X PUT "${BASE_URL}/partidos/${PARTIDO_ID}/confirmar" | python3 -m json.tool
echo ""

sleep 2

echo "======================================"
echo "❌ CANCELANDO PARTIDO..."
echo "Esto debe notificar a AMBOS usuarios"
echo "======================================"
curl -s -X PUT "${BASE_URL}/partidos/${PARTIDO_ID}/cancelar" \
  -H "Content-Type: application/json" \
  -d '{"motivo": "Test completado"}' | python3 -m json.tool
echo ""

echo "======================================"
echo "✅ TEST COMPLETADO"
echo "======================================"
echo ""
echo "👀 Revisa el terminal del backend para ver las simulaciones:"
echo "   - '=== SIMULACIÓN DE EMAIL ==='"
echo "   - '=== SIMULACIÓN DE PUSH NOTIFICATION ==='"
echo ""
