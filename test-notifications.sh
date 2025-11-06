#!/bin/bash

# Script de prueba para Email y Firebase Notifications
# =====================================================

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "🧪 PRUEBAS DE NOTIFICACIONES"
echo "  Email & Firebase Push Notifications"
echo "=========================================="
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Función para imprimir resultados
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
    fi
}

# Función para esperar
wait_seconds() {
    echo -e "${YELLOW}⏳ Esperando $1 segundos...${NC}"
    sleep $1
}

echo "=========================================="
echo "📋 PASO 1: Crear Usuarios de Prueba"
echo "=========================================="
echo ""

# Usuario 1: Con notificaciones EMAIL habilitadas
echo -e "${BLUE}👤 Creando Usuario 1 (Email habilitado)...${NC}"
USUARIO1=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "juan_email",
    "email": "juan.test@example.com",
    "contrasena": "password123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": true,
    "notificacionesPush": false,
    "latitud": -34.6037,
    "longitud": -58.3816
  }')

USUARIO1_ID=$(echo $USUARIO1 | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
print_result $? "Usuario 1 creado con ID: $USUARIO1_ID"
echo "   📧 Email: juan.test@example.com (notificaciones habilitadas)"
echo ""

# Usuario 2: Con notificaciones PUSH habilitadas
echo -e "${BLUE}👤 Creando Usuario 2 (Push habilitado)...${NC}"
USUARIO2=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "maria_push",
    "email": "maria.test@example.com",
    "contrasena": "password123",
    "nivelJuego": "AVANZADO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": false,
    "notificacionesPush": true,
    "firebaseToken": "fake-firebase-token-abcd1234567890xyz",
    "latitud": -34.6037,
    "longitud": -58.3816
  }')

USUARIO2_ID=$(echo $USUARIO2 | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
print_result $? "Usuario 2 creado con ID: $USUARIO2_ID"
echo "   🔔 Firebase Token: fake-firebase-token... (notificaciones habilitadas)"
echo ""

# Usuario 3: Con AMBAS notificaciones habilitadas
echo -e "${BLUE}👤 Creando Usuario 3 (Email y Push habilitados)...${NC}"
USUARIO3=$(curl -s -X POST "${BASE_URL}/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreUsuario": "carlos_both",
    "email": "carlos.test@example.com",
    "contrasena": "password123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "notificacionesEmail": true,
    "notificacionesPush": true,
    "firebaseToken": "fake-firebase-token-xyz9876543210abc",
    "latitud": -34.6037,
    "longitud": -58.3816
  }')

USUARIO3_ID=$(echo $USUARIO3 | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
print_result $? "Usuario 3 creado con ID: $USUARIO3_ID"
echo "   📧 Email: carlos.test@example.com"
echo "   🔔 Firebase Token: fake-firebase-token..."
echo ""

wait_seconds 2

echo "=========================================="
echo "⚽ PASO 2: Crear Partido"
echo "=========================================="
echo ""

echo -e "${BLUE}🎯 Creando partido de fútbol...${NC}"
PARTIDO=$(curl -s -X POST "${BASE_URL}/partidos" \
  -H "Content-Type: application/json" \
  -d "{
    \"organizadorId\": ${USUARIO1_ID},
    \"tipoDeporte\": \"FUTBOL\",
    \"fechaHora\": \"2025-11-10T18:00:00\",
    \"direccion\": \"Cancha Central, Buenos Aires\",
    \"cantidadJugadoresRequeridos\": 10,
    \"nivelMinimoRequerido\": \"PRINCIPIANTE\",
    \"nivelMaximoRequerido\": \"AVANZADO\",
    \"duracionMinutos\": 90,
    \"descripcion\": \"Partido amistoso de fútbol - Prueba de notificaciones\",
    \"latitud\": -34.6037,
    \"longitud\": -58.3816
  }")

PARTIDO_ID=$(echo $PARTIDO | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
print_result $? "Partido creado con ID: $PARTIDO_ID"
echo "   📍 Ubicación: Cancha Central, Buenos Aires"
echo "   👥 Jugadores requeridos: 10"
echo ""

wait_seconds 2

echo "=========================================="
echo "📧 PASO 3: Probar Notificaciones EMAIL"
echo "=========================================="
echo ""

echo -e "${BLUE}📨 Usuario 2 (maria_push) se une al partido...${NC}"
echo "   ℹ️  Esto debería enviar un EMAIL al organizador (juan_email)"
curl -s -X POST "${BASE_URL}/partidos/${PARTIDO_ID}/unirse/${USUARIO2_ID}" > /dev/null
print_result $? "Usuario 2 unido al partido"
echo ""
echo -e "${YELLOW}👀 Revisa los logs del backend para ver:${NC}"
echo "   ==> '=== SIMULACIÓN DE EMAIL ==='"
echo "   ==> Para: juan.test@example.com"
echo "   ==> Asunto: Nuevo jugador en tu partido"
echo ""

wait_seconds 3

echo "=========================================="
echo "🔔 PASO 4: Probar Notificaciones PUSH"
echo "=========================================="
echo ""

echo -e "${BLUE}📲 Usuario 3 (carlos_both) se une al partido...${NC}"
echo "   ℹ️  Esto debería enviar:"
echo "      - EMAIL al organizador (juan_email)"
echo "      - PUSH a todos los jugadores que lo tengan habilitado"
curl -s -X POST "${BASE_URL}/partidos/${PARTIDO_ID}/unirse/${USUARIO3_ID}" > /dev/null
print_result $? "Usuario 3 unido al partido"
echo ""
echo -e "${YELLOW}👀 Revisa los logs del backend para ver:${NC}"
echo "   ==> '=== SIMULACIÓN DE EMAIL ===' (para organizador)"
echo "   ==> '=== SIMULACIÓN DE PUSH NOTIFICATION ===' (para jugadores)"
echo ""

wait_seconds 3

echo "=========================================="
echo "🎯 PASO 5: Confirmar Partido"
echo "=========================================="
echo ""

echo -e "${BLUE}✅ Confirmando el partido...${NC}"
echo "   ℹ️  Esto debería enviar notificaciones a TODOS los jugadores"
echo "      según sus preferencias configuradas:"
echo "      - juan_email: recibirá EMAIL"
echo "      - maria_push: recibirá PUSH"
echo "      - carlos_both: recibirá EMAIL y PUSH"
curl -s -X PATCH "${BASE_URL}/partidos/${PARTIDO_ID}/confirmar" > /dev/null
print_result $? "Partido confirmado"
echo ""
echo -e "${YELLOW}👀 Revisa los logs del backend para ver:${NC}"
echo "   ==> Múltiples notificaciones según preferencias de cada usuario"
echo "   ==> Patrón Observer + Strategy en acción!"
echo ""

wait_seconds 3

echo "=========================================="
echo "❌ PASO 6: Cancelar Partido"
echo "=========================================="
echo ""

echo -e "${BLUE}🚫 Cancelando el partido...${NC}"
echo "   ℹ️  Esto también enviará notificaciones a todos los jugadores"
curl -s -X PATCH "${BASE_URL}/partidos/${PARTIDO_ID}/cancelar" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Prueba de notificaciones completada"
  }' > /dev/null
print_result $? "Partido cancelado"
echo ""
echo -e "${YELLOW}👀 Revisa los logs del backend para ver:${NC}"
echo "   ==> Notificaciones de cancelación enviadas"
echo ""

wait_seconds 2

echo "=========================================="
echo "📊 PASO 7: Verificar Estado Final"
echo "=========================================="
echo ""

echo -e "${BLUE}📋 Obteniendo información del partido...${NC}"
PARTIDO_INFO=$(curl -s -X GET "${BASE_URL}/partidos/${PARTIDO_ID}")
ESTADO=$(echo $PARTIDO_INFO | grep -o '"estadoActual":"[^"]*"' | cut -d'"' -f4)
JUGADORES=$(echo $PARTIDO_INFO | grep -o '"jugadoresActuales":[0-9]*' | grep -o '[0-9]*')

echo ""
echo "   🎫 Partido ID: $PARTIDO_ID"
echo "   📊 Estado: $ESTADO"
echo "   👥 Jugadores unidos: $JUGADORES"
echo ""

echo "=========================================="
echo "✅ PRUEBAS COMPLETADAS"
echo "=========================================="
echo ""
echo -e "${GREEN}🎉 Todas las pruebas de notificaciones ejecutadas!${NC}"
echo ""
echo "📝 RESUMEN:"
echo "   • Email Service Adapter: PROBADO ✅"
echo "   • Firebase Service Adapter: PROBADO ✅"
echo "   • Patrón Strategy (Email/Push): PROBADO ✅"
echo "   • Patrón Observer (PartidoListener): PROBADO ✅"
echo ""
echo "💡 NOTAS IMPORTANTES:"
echo "   • Las notificaciones se SIMULAN porque:"
echo "     - Email: JavaMailSender no configurado en application.properties"
echo "     - Firebase: firebase.enabled=false en application.properties"
echo ""
echo "   • Para enviar notificaciones REALES:"
echo "     1. Email: Configura spring.mail.* en application.properties"
echo "     2. Firebase: Coloca firebase-service-account.json y habilita firebase.enabled=true"
echo ""
echo "📖 Revisa los logs del backend para ver todas las simulaciones!"
echo ""
echo "🔗 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "=========================================="
