# Script de Testing Rápido
# Guarda este archivo como test-api.sh y ejecútalo después de iniciar la aplicación

#!/bin/bash

BASE_URL="http://localhost:8080"
echo "=========================================="
echo "Testing API Uno Mas"
echo "Base URL: $BASE_URL"
echo "=========================================="

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para hacer requests
test_endpoint() {
    echo ""
    echo "[$1] $2"
    response=$(curl -s -w "\n%{http_code}" $3)
    status=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$status" -ge 200 ] && [ "$status" -lt 300 ]; then
        echo -e "${GREEN}✓ Status: $status${NC}"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        echo -e "${RED}✗ Status: $status${NC}"
        echo "$body"
    fi
}

echo ""
echo "=== 1. REGISTRO DE USUARIOS ==="

test_endpoint "POST" "Registrar Usuario 1 (Organizador)" \
  "-X POST $BASE_URL/api/usuarios/registro \
   -H 'Content-Type: application/json' \
   -d '{
     \"nombreUsuario\": \"juan_organizador\",
     \"email\": \"juan@example.com\",
     \"contrasena\": \"password123\",
     \"deporteFavorito\": \"FUTBOL\",
     \"nivelJuego\": \"INTERMEDIO\",
     \"ubicacion\": \"-34.6037,-58.3816\"
   }'"

test_endpoint "POST" "Registrar Usuario 2" \
  "-X POST $BASE_URL/api/usuarios/registro \
   -H 'Content-Type: application/json' \
   -d '{
     \"nombreUsuario\": \"maria_jugadora\",
     \"email\": \"maria@example.com\",
     \"contrasena\": \"password123\",
     \"deporteFavorito\": \"FUTBOL\",
     \"nivelJuego\": \"AVANZADO\",
     \"ubicacion\": \"-34.6100,-58.3900\"
   }'"

test_endpoint "POST" "Registrar Usuario 3" \
  "-X POST $BASE_URL/api/usuarios/registro \
   -H 'Content-Type: application/json' \
   -d '{
     \"nombreUsuario\": \"carlos_principiante\",
     \"email\": \"carlos@example.com\",
     \"contrasena\": \"password123\",
     \"deporteFavorito\": \"FUTBOL\",
     \"nivelJuego\": \"PRINCIPIANTE\",
     \"ubicacion\": \"-34.6200,-58.4000\"
   }'"

echo ""
echo "=== 2. LISTAR USUARIOS ==="

test_endpoint "GET" "Obtener todos los usuarios" \
  "-X GET $BASE_URL/api/usuarios"

echo ""
echo "=== 3. CREAR PARTIDO ==="

test_endpoint "POST" "Crear Partido de Fútbol 5" \
  "-X POST $BASE_URL/api/partidos \
   -H 'Content-Type: application/json' \
   -d '{
     \"tipoDeporte\": \"FUTBOL_5\",
     \"cantidadJugadoresRequeridos\": 5,
     \"duracionMinutos\": 90,
     \"ubicacion\": \"-34.6037,-58.3816\",
     \"direccion\": \"Parque Centenario, Buenos Aires\",
     \"fechaHora\": \"2025-11-10T18:00:00\",
     \"organizadorId\": 1,
     \"permiteCualquierNivel\": true,
     \"descripcion\": \"Partido amistoso, todos los niveles bienvenidos\"
   }'"

echo ""
echo "=== 4. BUSCAR PARTIDOS ==="

test_endpoint "GET" "Buscar todos los partidos" \
  "-X GET $BASE_URL/api/partidos"

test_endpoint "GET" "Buscar partidos de FUTBOL" \
  "-X GET '$BASE_URL/api/partidos?tipoDeporte=FUTBOL_5'"

test_endpoint "GET" "Buscar con estrategia NIVEL (Usuario 2)" \
  "-X GET '$BASE_URL/api/partidos?estrategiaEmparejamiento=NIVEL&usuarioId=2'"

test_endpoint "GET" "Buscar con estrategia CERCANIA (Usuario 2)" \
  "-X GET '$BASE_URL/api/partidos?estrategiaEmparejamiento=CERCANIA&usuarioId=2'"

echo ""
echo "=== 5. UNIRSE AL PARTIDO ==="

test_endpoint "POST" "Usuario 2 se une al partido 1" \
  "-X POST $BASE_URL/api/partidos/1/unirse \
   -H 'Content-Type: application/json' \
   -d '{\"usuarioId\": 2}'"

test_endpoint "POST" "Usuario 3 se une al partido 1" \
  "-X POST $BASE_URL/api/partidos/1/unirse \
   -H 'Content-Type: application/json' \
   -d '{\"usuarioId\": 3}'"

echo ""
echo "=== 6. OBTENER DETALLE DEL PARTIDO ==="

test_endpoint "GET" "Ver estado del partido 1" \
  "-X GET $BASE_URL/api/partidos/1"

echo ""
echo "=== 7. CREAR MÁS USUARIOS PARA COMPLETAR EQUIPO ==="

for i in {4..5}; do
  test_endpoint "POST" "Registrar Usuario $i" \
    "-X POST $BASE_URL/api/usuarios/registro \
     -H 'Content-Type: application/json' \
     -d '{
       \"nombreUsuario\": \"jugador$i\",
       \"email\": \"jugador$i@example.com\",
       \"contrasena\": \"password123\",
       \"deporteFavorito\": \"FUTBOL\",
       \"nivelJuego\": \"INTERMEDIO\"
     }'"
  
  sleep 1
done

echo ""
echo "=== 8. COMPLETAR EL EQUIPO ==="

test_endpoint "POST" "Usuario 4 se une" \
  "-X POST $BASE_URL/api/partidos/1/unirse \
   -H 'Content-Type: application/json' \
   -d '{\"usuarioId\": 4}'"

test_endpoint "POST" "Usuario 5 se une (completa equipo)" \
  "-X POST $BASE_URL/api/partidos/1/unirse \
   -H 'Content-Type: application/json' \
   -d '{\"usuarioId\": 5}'"

echo ""
echo "=== 9. VERIFICAR CAMBIO DE ESTADO ==="

test_endpoint "GET" "Ver estado del partido (debería estar PARTIDO_ARMADO)" \
  "-X GET $BASE_URL/api/partidos/1"

echo ""
echo "=== 10. TRANSICIONES DE ESTADO ==="

test_endpoint "PUT" "Confirmar partido" \
  "-X PUT $BASE_URL/api/partidos/1/confirmar"

sleep 1

test_endpoint "PUT" "Iniciar partido" \
  "-X PUT $BASE_URL/api/partidos/1/iniciar"

sleep 1

test_endpoint "PUT" "Finalizar partido" \
  "-X PUT $BASE_URL/api/partidos/1/finalizar"

echo ""
echo "=== 11. CREAR PARTIDO PARA CANCELAR ==="

test_endpoint "POST" "Crear segundo partido" \
  "-X POST $BASE_URL/api/partidos \
   -H 'Content-Type: application/json' \
   -d '{
     \"tipoDeporte\": \"BASQUET\",
     \"cantidadJugadoresRequeridos\": 5,
     \"duracionMinutos\": 60,
     \"ubicacion\": \"-34.6037,-58.3816\",
     \"direccion\": \"Gimnasio Central\",
     \"fechaHora\": \"2025-11-12T19:00:00\",
     \"organizadorId\": 1
   }'"

sleep 1

test_endpoint "PUT" "Cancelar partido 2" \
  "-X PUT $BASE_URL/api/partidos/2/cancelar \
   -H 'Content-Type: application/json' \
   -d '{\"motivo\": \"Lluvia intensa\"}'"

echo ""
echo "=== 12. VER TODOS LOS PARTIDOS ==="

test_endpoint "GET" "Listar todos los partidos" \
  "-X GET $BASE_URL/api/partidos"

echo ""
echo "=========================================="
echo "Testing Completado!"
echo "=========================================="
echo ""
echo "Para ver los logs de notificaciones:"
echo "  - Revisar la consola donde corre la aplicación"
echo "  - Buscar líneas con 'SIMULACIÓN DE EMAIL' o 'SIMULACIÓN DE PUSH'"
echo ""
echo "Para acceder a Swagger UI:"
echo "  open http://localhost:8080/swagger-ui.html"
echo ""
