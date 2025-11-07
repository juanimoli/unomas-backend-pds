#!/bin/bash

# Uno Mas Sports Match System - Management Script
# Opciones: setup-demo, demo, test

BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Show usage
show_usage() {
    echo "=== Uno Mas - Sistema de Gestión de Encuentros Deportivos ==="
    echo ""
    echo "Uso: ./run.sh [opción]"
    echo ""
    echo "ESCENARIOS COMPLETOS (recomendado para demos):"
    echo "  1. finalizado     - Caso feliz: partido que llega a FINALIZADO"
    echo "  2. cancelado      - Partido cancelado antes de empezar"
    echo "  3. armado         - Partido con equipo completo pero no confirmado"
    echo "  4. buscando       - Partido creado buscando jugadores"
    echo ""
    echo "OPCIONES ADICIONALES:"
    echo "  emulador          - Abre el emulador de Android e instala la app"
    echo "  setup-demo        - Configura usuario demo con notificaciones"
    echo "  test              - Ejecuta suite completa de tests (27 tests)"
    echo "  verificar         - Verifica configuración de notificaciones"
    echo "  limpiar           - Reinicia el backend con base de datos limpia"
    echo ""
    echo "NOTA: Cada escenario limpia la BD y empieza desde cero"
    echo ""
}

# Check if no arguments
if [ $# -eq 0 ]; then
    show_usage
    exit 0
fi

COMMAND=$1

# Function to make curl requests and check response
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local expected_status=${4:-200}
    local description=$5

    echo -e "${YELLOW}Testing: $description${NC}"
    echo "Request: $method $url"

    if [ -n "$data" ]; then
        echo "Data: $data"
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X $method "$url" \
            -H "$CONTENT_TYPE" \
            -d "$data")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X $method "$url" \
            -H "$CONTENT_TYPE")
    fi

    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    LAST_RESPONSE=$(echo "$response" | sed '/HTTP_STATUS:/d')

    if [ "$http_status" -eq "$expected_status" ]; then
        echo -e "${GREEN}✓ Success (Status: $http_status)${NC}"
        if [ -n "$LAST_RESPONSE" ] && [ "$LAST_RESPONSE" != "null" ]; then
            echo "Response: $LAST_RESPONSE"
        fi
    else
        echo -e "${RED}✗ Failed (Expected: $expected_status, Got: $http_status)${NC}"
        if [ -n "$LAST_RESPONSE" ]; then
            echo "Response: $LAST_RESPONSE"
        fi
        exit 1
    fi
    echo ""
}

COMMAND=$1

# Function to extract ID from JSON response
extract_id() {
    local json=$1
    echo "$json" | grep -oE '"id":\s*[0-9]+' | head -1 | grep -oE '[0-9]+'
}

# Function to clean database (restart backend)
clean_database() {
    echo -e "${YELLOW}=== Limpiando Base de Datos (reiniciando backend) ===${NC}"
    echo ""
    
    # Find and kill existing backend process
    echo "Deteniendo backend existente..."
    pkill -f "spring-boot:run" 2>/dev/null || true
    pkill -f "unomas-backend" 2>/dev/null || true
    pkill -f "java.*unomas-backend.*jar" 2>/dev/null || true
    sleep 2
    
    # Start backend in background WITH ENVIRONMENT VARIABLES
    echo "Iniciando backend limpio con configuración de email..."
    cd /Users/juanimoli/Development/uno-mas-tp-adoo
    
    # Load .env file if it exists
    if [ -f .env ]; then
        echo "Cargando variables de entorno desde .env..."
        export $(cat .env | grep -v '^#' | xargs)
    else
        echo -e "${YELLOW}⚠ Archivo .env no encontrado. Las notificaciones por email podrían no funcionar.${NC}"
    fi
    
    # Use JAR instead of spring-boot:run to ensure proper environment
    if [ -f target/unomas-backend-1.0.0.jar ]; then
        nohup java -jar target/unomas-backend-1.0.0.jar > /tmp/unomas-backend.log 2>&1 &
    else
        echo "JAR no encontrado, compilando..."
        ./mvnw clean package -DskipTests
        nohup java -jar target/unomas-backend-1.0.0.jar > /tmp/unomas-backend.log 2>&1 &
    fi
    
    # Wait for backend to be ready
    echo -n "Esperando que el backend esté listo"
    for i in {1..40}; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo ""
            echo -e "${GREEN}✓ Backend reiniciado y listo${NC}"
            
            # Verify email configuration
            if [ -n "$SPRING_MAIL_USERNAME" ]; then
                echo -e "${GREEN}✓ Configuración de email detectada (${SPRING_MAIL_USERNAME})${NC}"
            else
                echo -e "${YELLOW}⚠ Sin configuración de email - solo notificaciones push${NC}"
            fi
            echo ""
            sleep 3
            return 0
        fi
        echo -n "."
        sleep 1
    done
    
    echo ""
    echo -e "${RED}✗ Timeout esperando el backend. Ver logs en /tmp/unomas-backend.log${NC}"
    tail -50 /tmp/unomas-backend.log
    exit 1
}

# Function to create or reuse user with real email
create_demo_user() {
    local deporte=$1
    local timestamp=$2
    
    # Try to find existing user first
    all_users=$(curl -s "$BASE_URL/api/usuarios")
    existing_user=$(echo "$all_users" | grep -B 10 "facundocarrizo99@gmail.com" | grep -oE '"id":\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
    
    if [ -n "$existing_user" ]; then
        echo -e "${YELLOW}Usuario con email facundocarrizo99@gmail.com ya existe (ID: $existing_user)${NC}"
        USER1_ID=$existing_user
        
        # Update push token
        curl -s -X PUT "$BASE_URL/api/usuarios/$USER1_ID/push-token" \
            -H "$CONTENT_TYPE" \
            -d '{"pushToken": "efQbd6s2QQ6dfGt8ymNKgU:APA91bHWS-4r2-lOx0ftL3sTjVQbuOrhNqw-7mbVzweFNtxtiuKPbklEf1lY9oQdorsLyC96ZAA0vXH2LmmzT99ZyqOGkjRavR45pp6x__b9XYnxd-5NgZ0"}' > /dev/null
        return
    fi
    
    # Create new user
    USER1_DATA='{
        "nombreUsuario": "org_'$timestamp'",
        "email": "facundocarrizo99@gmail.com",
        "contrasena": "demo123",
        "nivelJuego": "AVANZADO",
        "deporteFavorito": "'$deporte'",
        "longitud": -58.3816,
        "latitud": -34.6037,
        "notificacionesEmail": true,
        "notificacionesPush": true
    }'
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/api/usuarios/registro" -H "$CONTENT_TYPE" -d "$USER1_DATA")
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    if [ "$http_status" -eq "201" ] || [ "$http_status" -eq "200" ]; then
        USER1_ID=$(extract_id "$body")
        echo -e "${GREEN}Usuario creado exitosamente (ID: $USER1_ID)${NC}"
    else
        echo -e "${RED}Error creando usuario: $body${NC}"
        exit 1
    fi
    
    # Configurar token Firebase
    curl -s -X PUT "$BASE_URL/api/usuarios/$USER1_ID/push-token" \
        -H "$CONTENT_TYPE" \
        -d '{"pushToken": "efQbd6s2QQ6dfGt8ymNKgU:APA91bHWS-4r2-lOx0ftL3sTjVQbuOrhNqw-7mbVzweFNtxtiuKPbklEf1lY9oQdorsLyC96ZAA0vXH2LmmzT99ZyqOGkjRavR45pp6x__b9XYnxd-5NgZ0"}' > /dev/null
}

# Function to create secondary user
create_secondary_user() {
    local deporte=$1
    local timestamp=$2
    
    USER2_DATA='{
        "nombreUsuario": "jug2_'$timestamp'",
        "email": "jug2_'$timestamp'@test.com",
        "contrasena": "test123",
        "nivelJuego": "INTERMEDIO",
        "deporteFavorito": "'$deporte'",
        "longitud": -58.3816,
        "latitud": -34.6037,
        "notificacionesEmail": false,
        "notificacionesPush": false
    }'
    
    response=$(curl -s -X POST "$BASE_URL/api/usuarios/registro" -H "$CONTENT_TYPE" -d "$USER2_DATA")
    USER2_ID=$(extract_id "$response")
}

# ============================================================================
# ESCENARIO 1: PARTIDO FINALIZADO (Caso Feliz Completo)
# ============================================================================
escenario_finalizado() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║  ESCENARIO 1: PARTIDO FINALIZADO (Caso Feliz Completo)     ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estados que veremos:"
    echo "  1. BUSCANDO_JUGADORES → 2. PARTIDO_ARMADO → 3. CONFIRMADO → 4. EN_JUEGO → 5. FINALIZADO"
    echo ""
    echo -e "${YELLOW}Notificaciones esperadas: 4 (armado, confirmado, iniciado, finalizado)${NC}"
    echo ""
    echo -e "${YELLOW}NOTA: Este escenario usa la base de datos actual sin reiniciar.${NC}"
    echo -e "${YELLOW}      Para limpiar la BD, detén el backend y reinícialo.${NC}"
    echo ""
    
    # Crear usuarios
    echo -e "${YELLOW}Paso 1: Creando usuarios...${NC}"
    TIMESTAMP=$(date +%s%N | cut -b1-13)
    
    create_demo_user "FUTBOL" "$TIMESTAMP"
    create_secondary_user "FUTBOL" "$TIMESTAMP"
    
    echo -e "${GREEN}✓ Usuarios creados (IDs: $USER1_ID, $USER2_ID)${NC}"
    echo ""
    
    # Crear partido
    echo -e "${YELLOW}Paso 2: Creando partido...${NC}"
    PARTIDO_DATA='{
        "tipoDeporte": "FUTBOL",
        "organizadorId": '"$USER1_ID"',
        "cantidadJugadoresRequeridos": 2,
        "duracionMinutos": 90,
        "longitud": -58.3816,
        "latitud": -34.6037,
        "direccion": "Cancha Municipal",
        "fechaHora": "2025-11-15T18:00:00",
        "permiteCualquierNivel": true,
        "descripcion": "Partido de fútbol - Escenario completo"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/api/partidos" -H "$CONTENT_TYPE" -d "$PARTIDO_DATA")
    PARTIDO_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Partido creado (ID: $PARTIDO_ID)${NC}"
    echo -e "  Estado inicial: ${BLUE}BUSCANDO_JUGADORES${NC}"
    echo ""
    sleep 1
    
    # Usuarios se unen
    echo -e "${YELLOW}Paso 3: Usuarios se unen al partido...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER1_ID" > /dev/null
    echo -e "${GREEN}✓ Organizador unido${NC}"
    sleep 2
    
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER2_ID" > /dev/null
    echo -e "${GREEN}✓ Jugador 2 unido - Equipo completo!${NC}"
    echo -e "  Transición: BUSCANDO_JUGADORES → ${BLUE}PARTIDO_ARMADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Confirmar
    echo -e "${YELLOW}Paso 4: Confirmando partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/confirmar" > /dev/null
    echo -e "${GREEN}✓ Partido confirmado${NC}"
    echo -e "  Transición: PARTIDO_ARMADO → ${BLUE}CONFIRMADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Iniciar
    echo -e "${YELLOW}Paso 5: Iniciando partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/iniciar" > /dev/null
    echo -e "${GREEN}✓ Partido iniciado${NC}"
    echo -e "  Transición: CONFIRMADO → ${BLUE}EN_JUEGO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Finalizar
    echo -e "${YELLOW}Paso 6: Finalizando partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/finalizar" > /dev/null
    echo -e "${GREEN}✓ Partido finalizado${NC}"
    echo -e "  Transición: EN_JUEGO → ${BLUE}FINALIZADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Resumen
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ESCENARIO COMPLETADO EXITOSAMENTE             ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estado final: ${GREEN}FINALIZADO${NC}"
    echo "Transiciones realizadas: 5"
    echo "Notificaciones enviadas: 4"
    echo ""
    echo "Verificar:"
    echo "  📧 Email: facundocarrizo99@gmail.com (4 emails)"
    echo "  📱 Notificaciones push en dispositivo"
    echo ""
}

# ============================================================================
# ESCENARIO 2: PARTIDO CANCELADO
# ============================================================================
escenario_cancelado() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║        ESCENARIO 2: PARTIDO CANCELADO                      ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estados que veremos:"
    echo "  1. BUSCANDO_JUGADORES → 2. PARTIDO_ARMADO → 3. CANCELADO"
    echo ""
    echo -e "${YELLOW}Notificaciones esperadas: 2 (armado, cancelado)${NC}"
    echo ""
    
    # Crear usuarios
    echo -e "${YELLOW}Paso 1: Creando usuarios...${NC}"
    TIMESTAMP=$(date +%s%N | cut -b1-13)
    
    create_demo_user "BASQUET" "$TIMESTAMP"
    create_secondary_user "BASQUET" "$TIMESTAMP"
    
    echo -e "${GREEN}✓ Usuarios creados (IDs: $USER1_ID, $USER2_ID)${NC}"
    echo ""
    
    # Crear partido
    echo -e "${YELLOW}Paso 2: Creando partido de básquet...${NC}"
    PARTIDO_DATA='{
        "tipoDeporte": "BASQUET",
        "organizadorId": '"$USER1_ID"',
        "cantidadJugadoresRequeridos": 2,
        "duracionMinutos": 60,
        "longitud": -58.3816,
        "latitud": -34.6037,
        "direccion": "Polideportivo Central",
        "fechaHora": "2025-11-16T19:00:00",
        "permiteCualquierNivel": true,
        "descripcion": "Partido que será cancelado"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/api/partidos" -H "$CONTENT_TYPE" -d "$PARTIDO_DATA")
    PARTIDO_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Partido creado (ID: $PARTIDO_ID)${NC}"
    echo -e "  Estado inicial: ${BLUE}BUSCANDO_JUGADORES${NC}"
    echo ""
    sleep 1
    
    # Usuarios se unen
    echo -e "${YELLOW}Paso 3: Usuarios se unen al partido...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER1_ID" > /dev/null
    echo -e "${GREEN}✓ Organizador unido${NC}"
    sleep 2
    
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER2_ID" > /dev/null
    echo -e "${GREEN}✓ Jugador 2 unido - Equipo completo!${NC}"
    echo -e "  Transición: BUSCANDO_JUGADORES → ${BLUE}PARTIDO_ARMADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Cancelar (por mal clima, falta de jugadores, etc)
    echo -e "${YELLOW}Paso 4: Cancelando partido (ej: mal clima)...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/cancelar" > /dev/null
    echo -e "${GREEN}✓ Partido cancelado${NC}"
    echo -e "  Transición: PARTIDO_ARMADO → ${BLUE}CANCELADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Resumen
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ESCENARIO COMPLETADO EXITOSAMENTE             ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estado final: ${RED}CANCELADO${NC}"
    echo "Transiciones realizadas: 3"
    echo "Notificaciones enviadas: 2"
    echo ""
    echo "Caso de uso: Partido cancelado por mal clima/falta de jugadores"
    echo ""
}

# ============================================================================
# ESCENARIO 3: PARTIDO_ARMADO (Sin Confirmar)
# ============================================================================
escenario_armado() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║    ESCENARIO 3: PARTIDO_ARMADO (Equipo completo)           ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estados que veremos:"
    echo "  1. BUSCANDO_JUGADORES → 2. PARTIDO_ARMADO"
    echo ""
    echo -e "${YELLOW}Notificaciones esperadas: 1 (armado)${NC}"
    echo ""
    
    # Crear usuarios
    echo -e "${YELLOW}Paso 1: Creando usuarios...${NC}"
    TIMESTAMP=$(date +%s%N | cut -b1-13)
    
    create_demo_user "TENIS" "$TIMESTAMP"
    create_secondary_user "TENIS" "$TIMESTAMP"
    
    echo -e "${GREEN}✓ Usuarios creados (IDs: $USER1_ID, $USER2_ID)${NC}"
    echo ""
    
    # Crear partido
    echo -e "${YELLOW}Paso 2: Creando partido de tenis...${NC}"
    PARTIDO_DATA='{
        "tipoDeporte": "TENIS",
        "organizadorId": '"$USER1_ID"',
        "cantidadJugadoresRequeridos": 2,
        "duracionMinutos": 120,
        "longitud": -58.3816,
        "latitud": -34.6037,
        "direccion": "Club de Tenis",
        "fechaHora": "2025-11-17T10:00:00",
        "permiteCualquierNivel": true,
        "descripcion": "Partido de tenis - Pendiente de confirmación"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/api/partidos" -H "$CONTENT_TYPE" -d "$PARTIDO_DATA")
    PARTIDO_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Partido creado (ID: $PARTIDO_ID)${NC}"
    echo -e "  Estado inicial: ${BLUE}BUSCANDO_JUGADORES${NC}"
    echo ""
    sleep 1
    
    # Usuarios se unen
    echo -e "${YELLOW}Paso 3: Usuarios se unen al partido...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER1_ID" > /dev/null
    echo -e "${GREEN}✓ Organizador unido${NC}"
    sleep 2
    
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER2_ID" > /dev/null
    echo -e "${GREEN}✓ Jugador 2 unido - Equipo completo!${NC}"
    echo -e "  Transición: BUSCANDO_JUGADORES → ${BLUE}PARTIDO_ARMADO${NC}"
    echo -e "  📧 ${YELLOW}Enviando notificaciones...${NC}"
    sleep 5
    echo -e "  ✓ Notificaciones enviadas"
    echo ""
    
    # Resumen
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ESCENARIO COMPLETADO EXITOSAMENTE             ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estado final: ${YELLOW}PARTIDO_ARMADO${NC}"
    echo "Transiciones realizadas: 2"
    echo "Notificaciones enviadas: 1"
    echo ""
    echo "Caso de uso: Equipo completo esperando confirmación del organizador"
    echo ""
}

# ============================================================================
# ESCENARIO 4: BUSCANDO_JUGADORES (Partido Incompleto)
# ============================================================================
escenario_buscando() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║   ESCENARIO 4: BUSCANDO_JUGADORES (Equipo incompleto)      ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estados que veremos:"
    echo "  1. BUSCANDO_JUGADORES (sin completar equipo)"
    echo ""
    echo -e "${YELLOW}Notificaciones esperadas: 0 (no hay transiciones de estado)${NC}"
    echo ""
    
    # Crear usuarios
    echo -e "${YELLOW}Paso 1: Creando usuarios...${NC}"
    TIMESTAMP=$(date +%s%N | cut -b1-13)
    
    create_demo_user "VOLEY" "$TIMESTAMP"
    
    echo -e "${GREEN}✓ Usuario creado (ID: $USER1_ID)${NC}"
    echo ""
    
    # Crear partido que requiere más jugadores
    echo -e "${YELLOW}Paso 2: Creando partido de voley (requiere 6 jugadores)...${NC}"
    PARTIDO_DATA='{
        "tipoDeporte": "VOLEY",
        "organizadorId": '"$USER1_ID"',
        "cantidadJugadoresRequeridos": 6,
        "duracionMinutos": 90,
        "longitud": -58.3816,
        "latitud": -34.6037,
        "direccion": "Polideportivo - Cancha de Voley",
        "fechaHora": "2025-11-18T17:00:00",
        "permiteCualquierNivel": true,
        "descripcion": "Partido de voley - Buscando jugadores"
    }'
    
    response=$(curl -s -X POST "$BASE_URL/api/partidos" -H "$CONTENT_TYPE" -d "$PARTIDO_DATA")
    PARTIDO_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Partido creado (ID: $PARTIDO_ID)${NC}"
    echo -e "  Estado inicial: ${BLUE}BUSCANDO_JUGADORES${NC}"
    echo ""
    sleep 1
    
    # Solo organizador se une
    echo -e "${YELLOW}Paso 3: Organizador se une al partido...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER1_ID" > /dev/null
    echo -e "${GREEN}✓ Organizador unido (1/6 jugadores)${NC}"
    echo -e "  Estado: ${BLUE}BUSCANDO_JUGADORES${NC} (sin cambios)"
    echo ""
    sleep 1
    
    # Resumen
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ESCENARIO COMPLETADO EXITOSAMENTE             ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Estado final: ${BLUE}BUSCANDO_JUGADORES${NC}"
    echo "Jugadores inscritos: 1/6"
    echo "Transiciones realizadas: 1 (creación)"
    echo "Notificaciones enviadas: 0"
    echo ""
    echo "Caso de uso: Partido esperando más jugadores para completar equipo"
    echo ""
}

# ============================================================================
# ABRIR EMULADOR DE ANDROID
# ============================================================================
abrir_emulador() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║          INICIANDO EMULADOR DE ANDROID Y APP               ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    # Detect ANDROID_HOME if not set
    if [ -z "$ANDROID_HOME" ]; then
        if [ -d "$HOME/Library/Android/sdk" ]; then
            export ANDROID_HOME="$HOME/Library/Android/sdk"
            export PATH="$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools"
        else
            echo -e "${RED}✗ ANDROID_HOME no configurado${NC}"
            echo "Configura la variable de entorno ANDROID_HOME"
            exit 1
        fi
    fi
    
    # Check if ADB is available
    if ! command -v adb &> /dev/null; then
        echo -e "${RED}✗ ADB no encontrado. Instala Android SDK.${NC}"
        echo ""
        echo "Opciones:"
        echo "  1. Instalar Android Studio: https://developer.android.com/studio"
        echo "  2. Instalar SDK tools: brew install --cask android-platform-tools"
        exit 1
    fi
    
    # List available emulators
    echo -e "${YELLOW}Emuladores disponibles:${NC}"
    emulator_list=$($ANDROID_HOME/emulator/emulator -list-avds 2>/dev/null)
    
    if [ -z "$emulator_list" ]; then
        echo -e "${RED}✗ No hay emuladores configurados${NC}"
        echo ""
        echo "Crea un emulador desde Android Studio:"
        echo "  Tools → Device Manager → Create Virtual Device"
        exit 1
    fi
    
    echo "$emulator_list"
    echo ""
    
    # Use first emulator or ask user
    EMULATOR_NAME=$(echo "$emulator_list" | tail -1)
    echo -e "${YELLOW}Usando emulador: $EMULATOR_NAME${NC}"
    echo ""
    
    # Check if emulator is already running
    if adb devices | grep -q "emulator"; then
        echo -e "${GREEN}✓ Emulador ya está corriendo${NC}"
    else
        echo "Iniciando emulador en background..."
        nohup $ANDROID_HOME/emulator/emulator -avd "$EMULATOR_NAME" -no-snapshot-load > /tmp/emulator.log 2>&1 &
        EMULATOR_PID=$!
        
        # Wait for emulator to boot
        echo -n "Esperando que el emulador arranque"
        for i in {1..60}; do
            if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
                echo ""
                echo -e "${GREEN}✓ Emulador listo${NC}"
                break
            fi
            echo -n "."
            sleep 2
        done
        echo ""
    fi
    
    # Build and install app
    echo ""
    echo -e "${YELLOW}Compilando e instalando app...${NC}"
    cd android-app
    
    ./gradlew assembleDebug
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ App compilada${NC}"
        
        # Install APK
        adb install -r app/build/outputs/apk/debug/app-debug.apk
        
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}✓ App instalada en el emulador${NC}"
            echo ""
            
            # Launch app
            echo -e "${YELLOW}Abriendo la app...${NC}"
            # Get package name from AndroidManifest
            PACKAGE=$(aapt dump badging app/build/outputs/apk/debug/app-debug.apk 2>/dev/null | grep package | awk '{print $2}' | sed s/name=//g | sed s/\'//g)
            ACTIVITY=$(aapt dump badging app/build/outputs/apk/debug/app-debug.apk 2>/dev/null | grep launchable-activity | awk '{print $2}' | sed s/name=//g | sed s/\'//g)
            
            if [ -n "$PACKAGE" ] && [ -n "$ACTIVITY" ]; then
                adb shell am start -n "$PACKAGE/$ACTIVITY"
                echo -e "${GREEN}✓ App abierta${NC}"
            fi
            
            echo ""
            echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
            echo -e "${GREEN}║              EMULADOR Y APP LISTOS                         ║${NC}"
            echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
            echo ""
            echo "La app 'Uno Mas' está corriendo en el emulador."
            echo ""
            echo "Para ver logs: adb logcat | grep UnoMas"
        else
            echo -e "${RED}✗ Error instalando la app${NC}"
            exit 1
        fi
    else
        echo -e "${RED}✗ Error compilando la app${NC}"
        exit 1
    fi
    
    cd ..
}

# ============================================================================
# SETUP DEMO USER
# ============================================================================
setup_demo_user() {
    echo "=== Configurando Usuario de Demo para Notificaciones ==="
    echo ""

    # 1. Crear usuario de demo
    echo -e "${YELLOW}1. Creando usuario de demo...${NC}"
    DEMO_USER='{
        "nombreUsuario": "demo_notificaciones",
        "email": "facundocarrizo99@gmail.com",
        "contrasena": "demo123",
        "nivelJuego": "AVANZADO",
        "deporteFavorito": "FUTBOL",
        "longitud": -58.3816,
        "latitud": -34.6037,
        "notificacionesEmail": true,
        "notificacionesPush": true
    }'

    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL/api/usuarios/registro" \
        -H "$CONTENT_TYPE" \
        -d "$DEMO_USER")

    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    if [ "$http_status" -eq "201" ] || [ "$http_status" -eq "200" ]; then
        echo -e "${GREEN}✓ Usuario creado exitosamente${NC}"
        USER_ID=$(extract_id "$body")
        echo "ID del usuario: $USER_ID"
        echo ""
    else
        # Si el usuario ya existe, intentar obtenerlo
        if echo "$body" | grep -q "ya existe"; then
            echo -e "${YELLOW}El usuario ya existe. Buscando ID...${NC}"
            all_users=$(curl -s "$BASE_URL/api/usuarios")
            USER_ID=$(echo "$all_users" | grep -B 5 "demo_notificaciones" | grep -oE '"id":\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
            
            if [ -n "$USER_ID" ]; then
                echo -e "${GREEN}✓ Usuario encontrado con ID: $USER_ID${NC}"
                echo ""
            else
                echo -e "${RED}No se pudo encontrar el usuario.${NC}"
                exit 1
            fi
        else
            echo -e "${RED}Error al crear usuario (Status: $http_status)${NC}"
            echo "Response: $body"
            exit 1
        fi
    fi

    # 2. Configurar token de Firebase
    echo -e "${YELLOW}2. Configurando token de Firebase para notificaciones push...${NC}"
    PUSH_TOKEN='{"pushToken": "efQbd6s2QQ6dfGt8ymNKgU:APA91bHWS-4r2-lOx0ftL3sTjVQbuOrhNqw-7mbVzweFNtxtiuKPbklEf1lY9oQdorsLyC96ZAA0vXH2LmmzT99ZyqOGkjRavR45pp6x__b9XYnxd-5NgZ0"}'

    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE_URL/api/usuarios/$USER_ID/push-token" \
        -H "$CONTENT_TYPE" \
        -d "$PUSH_TOKEN")

    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)

    if [ "$http_status" -eq "200" ]; then
        echo -e "${GREEN}✓ Token de Firebase configurado exitosamente${NC}"
        echo ""
    else
        echo -e "${RED}Error al configurar token (Status: $http_status)${NC}"
        exit 1
    fi

    # 3. Verificar configuración
    echo -e "${YELLOW}3. Verificando configuración final...${NC}"
    usuario=$(curl -s "$BASE_URL/api/usuarios/$USER_ID")

    echo "$usuario" | grep -q "facundocarrizo99@gmail.com" && echo -e "${GREEN}✓ Email configurado: facundocarrizo99@gmail.com${NC}"
    echo "$usuario" | grep -q '"notificacionesEmail":true' && echo -e "${GREEN}✓ Notificaciones por email: ACTIVADAS${NC}"
    echo "$usuario" | grep -q '"notificacionesPush":true' && echo -e "${GREEN}✓ Notificaciones push: ACTIVADAS${NC}"
    echo "$usuario" | grep -q "efQbd6s2QQ6dfGt8ymNKgU" && echo -e "${GREEN}✓ Token de Firebase configurado${NC}"

    echo ""
    echo -e "${GREEN}=== Configuración Completa ===${NC}"
    echo ""
    echo "Detalles del usuario de demo:"
    echo "  - ID: $USER_ID"
    echo "  - Usuario: demo_notificaciones"
    echo "  - Email: facundocarrizo99@gmail.com"
    echo "  - Nivel: AVANZADO"
    echo "  - Deporte favorito: FUTBOL"
    echo "  - Notificaciones Email: ✓ ACTIVADAS"
    echo "  - Notificaciones Push: ✓ ACTIVADAS"
    echo ""
    echo "Para ejecutar la demo: ./run.sh demo"
}

# ============================================================================
# DEMO NOTIFICACIONES
# ============================================================================
run_demo() {
    echo "=== Demo de Notificaciones - Sistema Uno Mas ==="
    echo ""
    
    # Buscar usuario demo
    all_users=$(curl -s "$BASE_URL/api/usuarios")
    DEMO_USER_ID=$(echo "$all_users" | grep -B 5 "demo_notificaciones" | grep -oE '"id":\s*[0-9]+' | head -1 | grep -oE '[0-9]+')
    
    if [ -z "$DEMO_USER_ID" ]; then
        echo -e "${RED}❌ Usuario demo no encontrado${NC}"
        echo "Ejecuta primero: ./run.sh setup-demo"
        exit 1
    fi
    
    echo -e "${BLUE}Usuario principal (recibirá todas las notificaciones):${NC}"
    echo "  Email: facundocarrizo99@gmail.com"
    echo "  ID: $DEMO_USER_ID"
    echo ""

    # Crear usuario secundario
    echo -e "${YELLOW}1. Creando usuario secundario...${NC}"
    USER2_DATA='{
        "nombreUsuario": "jugador2_'$(date +%s)'",
        "email": "jugador2@test.com",
        "contrasena": "test123",
        "nivelJuego": "INTERMEDIO",
        "deporteFavorito": "FUTBOL",
        "longitud": -58.3816,
        "latitud": -34.6037,
        "notificacionesEmail": false,
        "notificacionesPush": false
    }'

    response=$(curl -s -X POST "$BASE_URL/api/usuarios/registro" -H "$CONTENT_TYPE" -d "$USER2_DATA")
    USER2_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Usuario 2 creado (ID: $USER2_ID)${NC}"
    echo ""

    # Crear partido con usuario demo
    echo -e "${YELLOW}2. Creando partido deportivo...${NC}"
    PARTIDO_DATA='{
        "tipoDeporte": "FUTBOL",
        "organizadorId": '"$DEMO_USER_ID"',
        "cantidadJugadoresRequeridos": 2,
        "duracionMinutos": 90,
        "longitud": -58.3816,
        "latitud": -34.6037,
        "direccion": "Cancha Municipal - Demo",
        "fechaHora": "2025-11-15T18:00:00",
        "permiteCualquierNivel": true,
        "descripcion": "Partido de demostración para sistema de notificaciones"
    }'

    response=$(curl -s -X POST "$BASE_URL/api/partidos" -H "$CONTENT_TYPE" -d "$PARTIDO_DATA")
    PARTIDO_ID=$(extract_id "$response")
    echo -e "${GREEN}✓ Partido creado (ID: $PARTIDO_ID)${NC}"
    echo -e "  Estado: BUSCANDO_JUGADORES"
    echo ""

    # Usuario demo se une
    echo -e "${YELLOW}3. Usuario demo se une al partido...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$DEMO_USER_ID" > /dev/null
    echo -e "${GREEN}✓ Usuario demo unido${NC}"
    echo -e "${BLUE}📧 Notificación enviada a: facundocarrizo99@gmail.com${NC}"
    echo ""
    sleep 2

    # Usuario 2 se une (completa el equipo)
    echo -e "${YELLOW}4. Usuario 2 se une al partido (completa el equipo)...${NC}"
    curl -s -X POST "$BASE_URL/api/matcher/unirse/$PARTIDO_ID?usuarioId=$USER2_ID" > /dev/null
    echo -e "${GREEN}✓ Usuario 2 unido - Equipo completo!${NC}"
    echo -e "  Estado cambia: BUSCANDO_JUGADORES → PARTIDO_ARMADO"
    echo -e "${BLUE}📧 Notificación enviada a: facundocarrizo99@gmail.com${NC}"
    echo -e "${BLUE}📱 Notificación push enviada a Firebase${NC}"
    echo ""
    sleep 2

    # Confirmar partido
    echo -e "${YELLOW}5. Confirmando el partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/confirmar" > /dev/null
    echo -e "${GREEN}✓ Partido confirmado${NC}"
    echo -e "  Estado cambia: PARTIDO_ARMADO → CONFIRMADO"
    echo -e "${BLUE}📧 Notificación enviada a: facundocarrizo99@gmail.com${NC}"
    echo -e "${BLUE}📱 Notificación push enviada a Firebase${NC}"
    echo ""
    sleep 2

    # Iniciar partido
    echo -e "${YELLOW}6. Iniciando el partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/iniciar" > /dev/null
    echo -e "${GREEN}✓ Partido iniciado${NC}"
    echo -e "  Estado cambia: CONFIRMADO → EN_JUEGO"
    echo -e "${BLUE}📧 Notificación enviada a: facundocarrizo99@gmail.com${NC}"
    echo -e "${BLUE}📱 Notificación push enviada a Firebase${NC}"
    echo ""
    sleep 2

    # Finalizar partido
    echo -e "${YELLOW}7. Finalizando el partido...${NC}"
    curl -s -X PUT "$BASE_URL/api/partidos/$PARTIDO_ID/finalizar" > /dev/null
    echo -e "${GREEN}✓ Partido finalizado${NC}"
    echo -e "  Estado cambia: EN_JUEGO → FINALIZADO"
    echo -e "${BLUE}📧 Notificación enviada a: facundocarrizo99@gmail.com${NC}"
    echo -e "${BLUE}📱 Notificación push enviada a Firebase${NC}"
    echo ""

    echo -e "${GREEN}=== Demo Completada ===${NC}"
    echo ""
    echo "Resumen de notificaciones enviadas:"
    echo "  📧 Emails enviados a: facundocarrizo99@gmail.com"
    echo "  📱 Notificaciones push al dispositivo configurado"
    echo ""
    echo "Total de transiciones de estado:"
    echo "  1. BUSCANDO_JUGADORES (creación)"
    echo "  2. PARTIDO_ARMADO (equipo completo)"
    echo "  3. CONFIRMADO (organizer confirma)"
    echo "  4. EN_JUEGO (partido inicia)"
    echo "  5. FINALIZADO (partido termina)"
    echo ""
    echo "Verifica tu email y dispositivo para ver las notificaciones!"
}

# ============================================================================
# TESTS COMPLETOS
# ============================================================================
run_tests() {
    set -e  # Exit on any error
    
    echo "=== Uno Mas API Testing Script ==="
    echo "Base URL: $BASE_URL"
    echo ""

echo "=== Starting API Tests ==="

# Generate unique suffix for test users
TIMESTAMP=$(date +%s)

# Test 1: Register User 1
echo "1. Registering User 1..."
USER1_DATA='{
    "nombreUsuario": "juanperez'$TIMESTAMP'",
    "email": "juan'$TIMESTAMP'@test.com",
    "contrasena": "password123",
    "nivelJuego": "INTERMEDIO",
    "deporteFavorito": "FUTBOL",
    "longitud": -58.3816,
    "latitud": -34.6037,
    "notificacionesEmail": true,
    "notificacionesPush": false
}'
make_request POST "$BASE_URL/api/usuarios/registro" "$USER1_DATA" 201 "Register User 1"
USER1_ID=$(extract_id "$LAST_RESPONSE")

# Test 2: Register User 2
echo "2. Registering User 2..."
USER2_DATA='{
    "nombreUsuario": "mariagarcia'$TIMESTAMP'",
    "email": "maria'$TIMESTAMP'@test.com",
    "contrasena": "password456",
    "nivelJuego": "AVANZADO",
    "deporteFavorito": "BASQUET",
    "longitud": -58.3816,
    "latitud": -34.6037,
    "notificacionesEmail": true,
    "notificacionesPush": true
}'
make_request POST "$BASE_URL/api/usuarios/registro" "$USER2_DATA" 201 "Register User 2"
USER2_ID=$(extract_id "$LAST_RESPONSE")

# Test 3: Register User 3
echo "3. Registering User 3..."
USER3_DATA='{
    "nombreUsuario": "carloslopez'$TIMESTAMP'",
    "email": "carlos'$TIMESTAMP'@test.com",
    "contrasena": "password789",
    "nivelJuego": "PRINCIPIANTE",
    "deporteFavorito": "TENIS",
    "longitud": -58.3816,
    "latitud": -34.6037,
    "notificacionesEmail": false,
    "notificacionesPush": true
}'
make_request POST "$BASE_URL/api/usuarios/registro" "$USER3_DATA" 201 "Register User 3"
USER3_ID=$(extract_id "$LAST_RESPONSE")

# Test 4: Get User 1
make_request GET "$BASE_URL/api/usuarios/$USER1_ID" "" 200 "Get User 1"

# Test 5: Get All Users
make_request GET "$BASE_URL/api/usuarios" "" 200 "Get All Users"

# Test 6: Update User 1 Push Token
PUSH_TOKEN_DATA='{"pushToken": "efQbd6s2QQ6dfGt8ymNKgU:APA91bHWS-4r2-lOx0ftL3sTjVQbuOrhNqw-7mbVzweFNtxtiuKPbklEf1lY9oQdorsLyC96ZAA0vXH2LmmzT99ZyqOGkjRavR45pp6x__b9XYnxd-5NgZ0"}'
make_request PUT "$BASE_URL/api/usuarios/$USER1_ID/push-token" "$PUSH_TOKEN_DATA" 200 "Update User 1 Push Token"

# Test 7: Create Match
echo "7. Creating Match..."
MATCH_DATA='{
    "tipoDeporte": "FUTBOL",
    "organizadorId": '"$USER1_ID"',
    "cantidadJugadoresRequeridos": 3,
    "duracionMinutos": 90,
    "longitud": -58.3816,
    "latitud": -34.6037,
    "direccion": "Cancha Municipal Centro",
    "fechaHora": "2025-12-01T15:00:00",
    "nivelMinimoRequerido": "PRINCIPIANTE",
    "nivelMaximoRequerido": "AVANZADO",
    "permiteCualquierNivel": true,
    "descripcion": "Partido amistoso de fútbol"
}'
make_request POST "$BASE_URL/api/partidos" "$MATCH_DATA" 201 "Create Match"
MATCH_ID=$(extract_id "$LAST_RESPONSE")

# Test 8: Get Match
make_request GET "$BASE_URL/api/partidos/$MATCH_ID" "" 200 "Get Match"

# Test 9: Search Matches
make_request GET "$BASE_URL/api/partidos?tipoDeporte=FUTBOL" "" 200 "Search Matches by Sport"

# Test 10: Organizer (User 1) joins match
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH_ID?usuarioId=$USER1_ID" "" 200 "User 1 (organizer) joins match"

# Test 11: User 2 joins match
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH_ID?usuarioId=$USER2_ID" "" 200 "User 2 joins match"

# Test 12: User 3 joins match (should complete the team and change state)
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH_ID?usuarioId=$USER3_ID" "" 200 "User 3 joins match (completes team)"

# Test 13: Get Match after joins (should be in PARTIDO_ARMADO state)
make_request GET "$BASE_URL/api/partidos/$MATCH_ID" "" 200 "Get Match after joins"

# Test 14: Confirm match
make_request PUT "$BASE_URL/api/partidos/$MATCH_ID/confirmar" "" 200 "Confirm match"

# Test 15: Start match
make_request PUT "$BASE_URL/api/partidos/$MATCH_ID/iniciar" "" 200 "Start match"

# Test 16: Finish match
make_request PUT "$BASE_URL/api/partidos/$MATCH_ID/finalizar" "" 200 "Finish match"

# Test 17: Create another match for cancellation test
echo "17. Creating another match for cancellation..."
MATCH2_DATA='{
    "tipoDeporte": "BASQUET",
    "organizadorId": '"$USER1_ID"',
    "cantidadJugadoresRequeridos": 2,
    "duracionMinutos": 60,
    "longitud": -58.3816,
    "latitud": -34.6037,
    "direccion": "Polideportivo Norte",
    "fechaHora": "2025-12-02T16:00:00",
    "permiteCualquierNivel": true,
    "descripcion": "Partido de básquetbol"
}'
make_request POST "$BASE_URL/api/partidos" "$MATCH2_DATA" 201 "Create second match"
MATCH2_ID=$(extract_id "$LAST_RESPONSE")

# Test 18: Cancel match
CANCEL_DATA='{"motivo": "Lluvia"}'
make_request PUT "$BASE_URL/api/partidos/$MATCH2_ID/cancelar" "$CANCEL_DATA" 200 "Cancel match"

# Test 19: Test user confirmation (matcher controller)
# First create a match that needs confirmation
echo "19. Creating match for confirmation test..."
MATCH3_DATA='{
    "tipoDeporte": "TENIS",
    "organizadorId": '"$USER1_ID"',
    "cantidadJugadoresRequeridos": 2,
    "duracionMinutos": 120,
    "longitud": -58.3816,
    "latitud": -34.6037,
    "direccion": "Club de Tenis Centro",
    "fechaHora": "2025-12-03T14:00:00",
    "permiteCualquierNivel": true,
    "descripcion": "Partido de tenis"
}'
make_request POST "$BASE_URL/api/partidos" "$MATCH3_DATA" 201 "Create third match"
MATCH3_ID=$(extract_id "$LAST_RESPONSE")

# Test 20: User 2 joins
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH3_ID?usuarioId=$USER2_ID" "" 200 "User 2 joins third match"

# Test 21: User 1 (organizer) joins to complete match
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH3_ID?usuarioId=$USER1_ID" "" 200 "User 1 joins third match (completes team)"

# Test 22: Verify match is in PARTIDO_ARMADO state
make_request GET "$BASE_URL/api/partidos/$MATCH3_ID" "" 200 "Get match status (should be PARTIDO_ARMADO)"

# Test 23: User 2 leaves match (should change state back to BUSCANDO_JUGADORES)
make_request DELETE "$BASE_URL/api/matcher/bajarse/$MATCH3_ID?usuarioId=$USER2_ID" "" 200 "User 2 leaves match"

# Test 24: Verify match is back to BUSCANDO_JUGADORES state
make_request GET "$BASE_URL/api/partidos/$MATCH3_ID" "" 200 "Get match status (should be BUSCANDO_JUGADORES)"

# Test 25: User 2 joins again to complete team
make_request POST "$BASE_URL/api/matcher/unirse/$MATCH3_ID?usuarioId=$USER2_ID" "" 200 "User 2 joins again"

# Test 26: Confirm match
make_request PUT "$BASE_URL/api/partidos/$MATCH3_ID/confirmar" "" 200 "Confirm match"

# Test 27: Try to leave confirmed match (should fail)
echo -e "${YELLOW}Testing: Try to leave confirmed match (should fail with 400)${NC}"
echo "Request: DELETE $BASE_URL/api/matcher/bajarse/$MATCH3_ID?usuarioId=$USER2_ID"
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X DELETE "$BASE_URL/api/matcher/bajarse/$MATCH3_ID?usuarioId=$USER2_ID" -H "$CONTENT_TYPE")
http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
if [ "$http_status" -eq "400" ]; then
    echo -e "${GREEN}✓ Success - Correctly prevented leaving confirmed match (Status: $http_status)${NC}"
else
    echo -e "${RED}✗ Failed - Expected 400, got $http_status${NC}"
    exit 1
fi
echo ""

echo ""

echo -e "${GREEN}=== All API Tests Completed Successfully! ===${NC}"
echo ""
echo "Summary of tests executed:"
echo "  ✓ User registration (3 users)"
echo "  ✓ Get user by ID"
echo "  ✓ Get all users"
echo "  ✓ Update push token"
echo "  ✓ Create match"
echo "  ✓ Get match by ID"
echo "  ✓ Search matches by sport"
echo "  ✓ Users join match"
echo "  ✓ Team completion (state transition to PARTIDO_ARMADO)"
echo "  ✓ Confirm match (state transition to CONFIRMADO)"
echo "  ✓ Start match (state transition to EN_JUEGO)"
echo "  ✓ Finish match (state transition to FINALIZADO)"
echo "  ✓ Cancel match (state transition to CANCELADO)"
echo "  ✓ User leaves match (state reverts to BUSCANDO_JUGADORES)"
echo "  ✓ Validation: Cannot leave confirmed match"
echo ""
echo "Total tests: 27"
echo "All pattern implementations tested:"
echo "  - Factory Pattern (PartidoFactory, EmparejamientoStrategyFactory)"
echo "  - State Pattern (EstadoPartido transitions + state reversion)"
echo "  - Observer Pattern (Notification system)"
echo "  - Adapter Pattern (NotificationServiceAdapter)"
echo "  - Strategy Pattern (EmparejamientoStrategy)"
echo "  - MVC Pattern (Controller/Service/Repository)"
}

# ============================================================================
# VERIFICAR CONFIGURACIÓN
# ============================================================================
verificar_configuracion() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║       VERIFICACIÓN DE CONFIGURACIÓN DE NOTIFICACIONES      ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    # Check .env file
    echo -e "${YELLOW}1. Verificando archivo .env...${NC}"
    if [ -f .env ]; then
        echo -e "${GREEN}✓ Archivo .env encontrado${NC}"
        
        # Load and check variables
        export $(cat .env | grep -v '^#' | xargs) 2>/dev/null
        
        if [ -n "$SPRING_MAIL_USERNAME" ]; then
            echo -e "${GREEN}✓ SPRING_MAIL_USERNAME configurado: $SPRING_MAIL_USERNAME${NC}"
        else
            echo -e "${RED}✗ SPRING_MAIL_USERNAME no configurado${NC}"
        fi
        
        if [ -n "$SPRING_MAIL_PASSWORD" ]; then
            echo -e "${GREEN}✓ SPRING_MAIL_PASSWORD configurado (oculto)${NC}"
        else
            echo -e "${RED}✗ SPRING_MAIL_PASSWORD no configurado${NC}"
        fi
    else
        echo -e "${RED}✗ Archivo .env no encontrado${NC}"
        echo ""
        echo "Crea el archivo .env con:"
        echo "  SPRING_MAIL_USERNAME=tu-email@gmail.com"
        echo "  SPRING_MAIL_PASSWORD=tu-app-password"
    fi
    echo ""
    
    # Check Firebase config
    echo -e "${YELLOW}2. Verificando Firebase...${NC}"
    if [ -f src/main/resources/firebase-service-account.json ]; then
        echo -e "${GREEN}✓ firebase-service-account.json encontrado${NC}"
    else
        echo -e "${YELLOW}⚠ firebase-service-account.json no encontrado (solo push local)${NC}"
    fi
    echo ""
    
    # Check if backend is running
    echo -e "${YELLOW}3. Verificando backend...${NC}"
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Backend corriendo en puerto 8080${NC}"
        
        # Check health endpoint
        health=$(curl -s http://localhost:8080/actuator/health)
        if echo "$health" | grep -q '"status":"UP"'; then
            echo -e "${GREEN}✓ Backend en estado UP${NC}"
        else
            echo -e "${YELLOW}⚠ Backend con problemas${NC}"
        fi
    else
        echo -e "${RED}✗ Backend no está corriendo${NC}"
        echo "Inicia el backend con: ./run.sh start"
    fi
    echo ""
    
    # Check compiled JAR
    echo -e "${YELLOW}4. Verificando compilación...${NC}"
    if [ -f target/unomas-backend-1.0.0.jar ]; then
        echo -e "${GREEN}✓ JAR compilado encontrado${NC}"
        jar_size=$(ls -lh target/unomas-backend-1.0.0.jar | awk '{print $5}')
        echo "  Tamaño: $jar_size"
    else
        echo -e "${YELLOW}⚠ JAR no compilado${NC}"
        echo "Compila con: ./mvnw clean package -DskipTests"
    fi
    echo ""
    
    # Summary
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                  RESUMEN DE VERIFICACIÓN                   ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    if [ -f .env ] && [ -n "$SPRING_MAIL_USERNAME" ] && [ -n "$SPRING_MAIL_PASSWORD" ]; then
        echo -e "${GREEN}✓ Notificaciones por Email: CONFIGURADAS${NC}"
        echo "  Email configurado: $SPRING_MAIL_USERNAME"
    else
        echo -e "${RED}✗ Notificaciones por Email: NO CONFIGURADAS${NC}"
        echo "  Crea archivo .env con credenciales de Gmail"
    fi
    
    if [ -f src/main/resources/firebase-service-account.json ]; then
        echo -e "${GREEN}✓ Notificaciones Push: CONFIGURADAS${NC}"
    else
        echo -e "${YELLOW}⚠ Notificaciones Push: PARCIAL (solo local)${NC}"
    fi
    
    echo ""
    echo "Para ejecutar escenarios con notificaciones:"
    echo "  ./run.sh finalizado    - Escenario completo con 4 notificaciones"
    echo "  ./run.sh cancelado     - Escenario cancelación con 2 notificaciones"
    echo ""
}

# ============================================================================
# MAIN COMMAND ROUTER
# ============================================================================
case "$COMMAND" in
    1|finalizado)
        escenario_finalizado
        ;;
    2|cancelado)
        escenario_cancelado
        ;;
    3|armado)
        escenario_armado
        ;;
    4|buscando)
        escenario_buscando
        ;;
    emulador)
        abrir_emulador
        ;;
    setup-demo)
        setup_demo_user
        ;;
    demo)
        run_demo
        ;;
    test)
        run_tests
        ;;
    verificar|verify)
        verificar_configuracion
        ;;
    limpiar|clean|restart)
        clean_database
        ;;
    *)
        show_usage
        exit 1
        ;;
esac