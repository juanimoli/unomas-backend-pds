#!/bin/bash

# Test COMPLETO de notificaciones Email y Firebase
BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "🧪 TEST COMPLETO DE NOTIFICACIONES"
echo "   Email & Firebase Push"
echo "=========================================="
echo ""

# Usuario 1: Organizador con EMAIL
echo "👤 Creando Usuario 1 (Organizador - EMAIL)..."
U1=$(curl -s -X POST "${BASE_URL}/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "organizador_email",
    "email": "org@test.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": true,
    "notificacionesPush": false
  }')
U1_ID=$(echo "$U1" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Usuario 1 ID: $U1_ID (Email: org@test.com)"
echo ""

# Usuario 2: con PUSH
echo "👤 Creando Usuario 2 (PUSH)..."
U2=$(curl -s -X POST "${BASE_URL}/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "jugador_push",
    "email": "push@test.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": false,
    "notificacionesPush": true,
    "firebaseToken": "FCM-TOKEN-FAKE-12345678"
  }')
U2_ID=$(echo "$U2" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Usuario 2 ID: $U2_ID (Firebase: FCM-TOKEN-FAKE...)"
echo ""

# Usuario 3: con AMBOS
echo "👤 Creando Usuario 3 (EMAIL y PUSH)..."
U3=$(curl -s -X POST "${BASE_URL}/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "jugador_ambos",
    "email": "both@test.com",
    "contrasena": "pass123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": true,
    "notificacionesPush": true,
    "firebaseToken": "FCM-TOKEN-FAKE-87654321"
  }')
U3_ID=$(echo "$U3" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Usuario 3 ID: $U3_ID (Email: both@test.com + Firebase)"
echo ""

# Crear partido que NO necesita muchos jugadores
echo "⚽ Creando partido de FUTBOL..."
PARTIDO=$(curl -s -X POST "${BASE_URL}/partidos" \
  -H "Content-Type: application/json" \
  -d "{
    \"organizadorId\": ${U1_ID},
    \"tipoDeporte\": \"FUTBOL\",
    \"fechaHora\": \"2025-11-20T18:00:00\",
    \"direccion\": \"Cancha Central\",
    \"cantidadJugadoresRequeridos\": 2,
    \"nivelMinimoRequerido\": \"PRINCIPIANTE\",
    \"nivelMaximoRequerido\": \"AVANZADO\",
    \"duracionMinutos\": 90,
    \"descripcion\": \"Partido de prueba\",
    \"permiteCualquierNivel\": false
  }")
PID=$(echo "$PARTIDO" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "✅ Partido ID: $PID (necesita 2 jugadores)"
echo ""

sleep 1

echo "=========================================="
echo "📧 TEST 1: Notificación EMAIL"
echo "=========================================="
echo "Usuario 2 (push) se une → debe notificar al organizador (email)"
echo ""
curl -s -X POST "${BASE_URL}/partidos/${PID}/unirse" \
  -H "Content-Type: application/json" \
  -d "{\"usuarioId\": ${U2_ID}}" > /dev/null
echo "✅ Usuario 2 se unió al partido"
echo ""
echo "👀 Busca en los logs del backend:"
echo "   '=== SIMULACIÓN DE EMAIL ==='"
echo "   'Para: org@test.com'"
echo "   'Asunto: Nuevo jugador en tu partido'"
echo ""

sleep 3

echo "=========================================="
echo "📧+🔔 TEST 2: Notificaciones MIXTAS"
echo "=========================================="
echo "Usuario 3 (ambos) se une → notificará:"
echo "  - EMAIL al organizador"
echo "  - PUSH a los jugadores con Firebase habilitado"
echo ""
curl -s -X POST "${BASE_URL}/partidos/${PID}/unirse" \
  -H "Content-Type: application/json" \
  -d "{\"usuarioId\": ${U3_ID}}" > /dev/null
echo "✅ Usuario 3 se unió al partido"
echo ""
echo "👀 Busca en los logs:"
echo "   '=== SIMULACIÓN DE EMAIL ===' (para organizador)"
echo "   '=== SIMULACIÓN DE PUSH NOTIFICATION ===' (para jugadores)"
echo ""

sleep 3

echo "=========================================="
echo "🎯 TEST 3: Confirmar Partido"
echo "=========================================="
echo "Partido completo → notifica a TODOS según sus preferencias:"
echo "  - org@test.com: EMAIL ✉️"
echo "  - jugador_push: PUSH 🔔"
echo "  - jugador_ambos: EMAIL ✉️ + PUSH 🔔"
echo ""
curl -s -X PUT "${BASE_URL}/partidos/${PID}/confirmar" > /dev/null 2>&1
echo "✅ Partido confirmado"
echo ""
echo "👀 Busca en los logs:"
echo "   Múltiples '=== SIMULACIÓN DE EMAIL ==='"
echo "   Múltiples '=== SIMULACIÓN DE PUSH NOTIFICATION ==='"
echo "   ⚡ Patrón Observer + Strategy en acción!"
echo ""

sleep 3

echo "=========================================="
echo "❌ TEST 4: Cancelar Partido"
echo "=========================================="
echo "Cancelación → notifica a todos los jugadores"
echo ""
curl -s -X PUT "${BASE_URL}/partidos/${PID}/cancelar" \
  -H "Content-Type: application/json" \
  -d '{"motivo": "Prueba de notificaciones completada"}' > /dev/null
echo "✅ Partido cancelado"
echo ""
echo "👀 Busca en los logs:"
echo "   '=== SIMULACIÓN DE EMAIL ===' (cancelación)"
echo "   '=== SIMULACIÓN DE PUSH NOTIFICATION ===' (cancelación)"
echo ""

sleep 2

echo "=========================================="
echo "📊 VERIFICACIÓN FINAL"
echo "=========================================="
RESULT=$(curl -s -X GET "${BASE_URL}/partidos/${PID}")
ESTADO=$(echo "$RESULT" | grep -o '"estadoActual":"[^"]*"' | cut -d'"' -f4)
JUG=$(echo "$RESULT" | grep -o '"jugadores":\[[^]]*\]' | grep -o '"id":[0-9]*' | wc -l | tr -d ' ')
echo ""
echo "🎫 Partido: $PID"
echo "📊 Estado: $ESTADO"
echo "👥 Jugadores: $JUG"
echo ""

echo "=========================================="
echo "✅ PRUEBAS COMPLETADAS"
echo "=========================================="
echo ""
echo "📝 RESUMEN DE PATRONES PROBADOS:"
echo ""
echo "  🎯 Adapter Pattern:"
echo "     • EmailServiceAdapter - Adapta JavaMailSender"
echo "     • FirebaseServiceAdapter - Adapta Firebase Messaging"
echo ""
echo "  🎯 Strategy Pattern:"
echo "     • EmailNotificationStrategy - Envío por email"
echo "     • PushNotificationStrategy - Envío por push"
echo ""
echo "  🎯 Observer Pattern:"
echo "     • PartidoListener - Escucha eventos de partido"
echo "     • Notifica a usuarios según sus preferencias"
echo ""
echo "💡 CONFIGURACIÓN ACTUAL:"
echo "   • Email: SIMULADO (JavaMailSender no configurado)"
echo "   • Firebase: SIMULADO (firebase.enabled=false)"
echo ""
echo "📖 Para ver todas las notificaciones simuladas,"
echo "   revisa el output del terminal donde corre el backend"
echo ""
echo "🔗 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "=========================================="
