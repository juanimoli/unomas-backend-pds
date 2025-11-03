#!/bin/bash

# Script de Testing API Uno Mas - Versión Simplificada

BASE_URL="http://localhost:8080"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

clear
echo "=========================================="
echo "Testing API Uno Mas"
echo "Base URL: $BASE_URL"
echo "=========================================="

# Test 1: Registrar usuarios
echo ""
echo -e "${BLUE}=== 1. REGISTRO DE USUARIOS ===${NC}"
echo ""

echo -e "${YELLOW}[POST]${NC} Registrar Usuario 1 (Organizador)"
RESP1=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"juan_organizador","email":"juan@example.com","contrasena":"password123","deporteFavorito":"FUTBOL","nivelJuego":"INTERMEDIO","ubicacion":"-34.6037,-58.3816","notificacionesEmail":true,"notificacionesPush":false}')
STATUS1=$(echo "$RESP1" | tail -n1)
BODY1=$(echo "$RESP1" | sed '$d')
if [ "$STATUS1" -ge 200 ] && [ "$STATUS1" -lt 300 ]; then
    echo -e "${GREEN}✓ Status: $STATUS1${NC}"
    echo "$BODY1" | jq '.' 2>/dev/null || echo "$BODY1"
else
    echo -e "${RED}✗ Status: $STATUS1${NC}"
    echo "$BODY1"
fi

echo ""
echo -e "${YELLOW}[POST]${NC} Registrar Usuario 2"
curl -s -X POST "$BASE_URL/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"maria_jugadora","email":"maria@example.com","contrasena":"password123","deporteFavorito":"FUTBOL","nivelJuego":"AVANZADO","ubicacion":"-34.6100,-58.3900","notificacionesEmail":true,"notificacionesPush":false}' | jq '.'

echo ""
echo -e "${YELLOW}[POST]${NC} Registrar Usuario 3"
curl -s -X POST "$BASE_URL/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"carlos_principiante","email":"carlos@example.com","contrasena":"password123","deporteFavorito":"FUTBOL","nivelJuego":"PRINCIPIANTE","ubicacion":"-34.6200,-58.4000","notificacionesEmail":true,"notificacionesPush":false}' | jq '.'

# Test 2: Listar usuarios
echo ""
echo -e "${BLUE}=== 2. LISTAR USUARIOS ===${NC}"
echo ""
echo -e "${YELLOW}[GET]${NC} Obtener todos los usuarios"
curl -s "$BASE_URL/api/usuarios" | jq '.'

# Test 3: Crear partido
echo ""
echo -e "${BLUE}=== 3. CREAR PARTIDO ===${NC}"
echo ""
echo -e "${YELLOW}[POST]${NC} Crear Partido de Fútbol 5"
curl -s -X POST "$BASE_URL/api/partidos" \
  -H "Content-Type: application/json" \
  -d '{"tipoDeporte":"FUTBOL_5","cantidadJugadoresRequeridos":5,"duracionMinutos":90,"ubicacion":"-34.6037,-58.3816","direccion":"Parque Centenario, Buenos Aires","fechaHora":"2025-11-10T18:00:00","organizadorId":1,"permiteCualquierNivel":true,"descripcion":"Partido amistoso"}' | jq '.'

# Test 4: Buscar partidos
echo ""
echo -e "${BLUE}=== 4. BUSCAR PARTIDOS ===${NC}"
echo ""
echo -e "${YELLOW}[GET]${NC} Buscar todos los partidos"
curl -s "$BASE_URL/api/partidos" | jq '.'

echo ""
echo -e "${YELLOW}[GET]${NC} Buscar partidos de FUTBOL_5"
curl -s "$BASE_URL/api/partidos?tipoDeporte=FUTBOL_5" | jq '.'

# Test 5: Unirse al partido
echo ""
echo -e "${BLUE}=== 5. UNIRSE AL PARTIDO ===${NC}"
echo ""
echo -e "${YELLOW}[POST]${NC} Usuario 2 se une al partido 1"
curl -s -X POST "$BASE_URL/api/partidos/1/unirse" \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":2}' | jq '.'

echo ""
echo -e "${YELLOW}[POST]${NC} Usuario 3 se une al partido 1"
curl -s -X POST "$BASE_URL/api/partidos/1/unirse" \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":3}' | jq '.'

# Test 6: Ver detalle del partido
echo ""
echo -e "${BLUE}=== 6. OBTENER DETALLE DEL PARTIDO ===${NC}"
echo ""
echo -e "${YELLOW}[GET]${NC} Ver estado del partido 1"
curl -s "$BASE_URL/api/partidos/1" | jq '.'

# Test 7: Crear más usuarios
echo ""
echo -e "${BLUE}=== 7. CREAR MÁS USUARIOS ===${NC}"
echo ""
echo -e "${YELLOW}[POST]${NC} Registrar Usuario 4"
curl -s -X POST "$BASE_URL/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"jugador4","email":"jugador4@example.com","contrasena":"password123","deporteFavorito":"FUTBOL","nivelJuego":"INTERMEDIO","notificacionesEmail":true,"notificacionesPush":false}' | jq '.'

sleep 1

echo ""
echo -e "${YELLOW}[POST]${NC} Registrar Usuario 5"
curl -s -X POST "$BASE_URL/api/usuarios/registro" \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"jugador5","email":"jugador5@example.com","contrasena":"password123","deporteFavorito":"FUTBOL","nivelJuego":"INTERMEDIO","notificacionesEmail":true,"notificacionesPush":false}' | jq '.'

# Test 8: Completar equipo
echo ""
echo -e "${BLUE}=== 8. COMPLETAR EL EQUIPO ===${NC}"
echo ""

sleep 1

echo -e "${YELLOW}[POST]${NC} Usuario 4 se une"
curl -s -X POST "$BASE_URL/api/partidos/1/unirse" \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":4}' | jq '.'

echo ""
echo -e "${YELLOW}[POST]${NC} Usuario 5 se une (completa equipo)"
curl -s -X POST "$BASE_URL/api/partidos/1/unirse" \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":5}' | jq '.'

# Test 9: Verificar cambio de estado
echo ""
echo -e "${BLUE}=== 9. VERIFICAR CAMBIO DE ESTADO ===${NC}"
echo ""
echo -e "${YELLOW}[GET]${NC} Ver estado del partido (debería estar PARTIDO_ARMADO)"
curl -s "$BASE_URL/api/partidos/1" | jq '.'

# Test 10: Transiciones de estado
echo ""
echo -e "${BLUE}=== 10. TRANSICIONES DE ESTADO ===${NC}"
echo ""

echo -e "${YELLOW}[PUT]${NC} Confirmar partido"
curl -s -X PUT "$BASE_URL/api/partidos/1/confirmar" | jq '.'

sleep 1

echo ""
echo -e "${YELLOW}[PUT]${NC} Iniciar partido"
curl -s -X PUT "$BASE_URL/api/partidos/1/iniciar" | jq '.'

sleep 1

echo ""
echo -e "${YELLOW}[PUT]${NC} Finalizar partido"
curl -s -X PUT "$BASE_URL/api/partidos/1/finalizar" | jq '.'

# Test 11: Crear partido para cancelar
echo ""
echo -e "${BLUE}=== 11. CREAR PARTIDO PARA CANCELAR ===${NC}"
echo ""

sleep 1

echo -e "${YELLOW}[POST]${NC} Crear segundo partido"
curl -s -X POST "$BASE_URL/api/partidos" \
  -H "Content-Type: application/json" \
  -d '{"tipoDeporte":"BASQUET","cantidadJugadoresRequeridos":5,"duracionMinutos":60,"ubicacion":"-34.6037,-58.3816","direccion":"Gimnasio Central","fechaHora":"2025-11-12T19:00:00","organizadorId":1,"permiteCualquierNivel":true}' | jq '.'

sleep 1

echo ""
echo -e "${YELLOW}[PUT]${NC} Cancelar partido 2"
curl -s -X PUT "$BASE_URL/api/partidos/2/cancelar" \
  -H "Content-Type: application/json" \
  -d '{"motivo":"Lluvia intensa"}' | jq '.'

# Test 12: Ver todos los partidos
echo ""
echo -e "${BLUE}=== 12. VER TODOS LOS PARTIDOS ===${NC}"
echo ""
echo -e "${YELLOW}[GET]${NC} Listar todos los partidos"
curl -s "$BASE_URL/api/partidos" | jq '.'

# Resumen final
echo ""
echo "=========================================="
echo -e "${GREEN}Testing Completado!${NC}"
echo "=========================================="
echo ""
echo -e "${BLUE}Para ver más detalles:${NC}"
echo "  - Logs del backend: tail -f backend.log"
echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
echo "  - H2 Console: http://localhost:8080/h2-console"
echo ""
