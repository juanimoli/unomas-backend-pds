#!/bin/bash

BASE_URL="http://localhost:8080"

case "$1" in
  start)
    echo "🚀 Iniciando backend..."
    ./mvnw clean package -DskipTests
    java -jar target/unomas-backend-1.0.0.jar
    ;;
    
  test)
    echo "🧪 Probando API..."
    
    echo "1. Crear usuario..."
    U1=$(curl -s -X POST "$BASE_URL/api/usuarios/registro" \
      -H "Content-Type: application/json" \
      -d '{
        "nombreUsuario": "user_'$(date +%s)'",
        "email": "test@example.com",
        "contrasena": "pass123",
        "nivelJuego": "INTERMEDIO",
        "deporteFavorito": "FUTBOL",
        "notificacionesEmail": true,
        "notificacionesPush": false
      }')
    
    U1_ID=$(echo "$U1" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo "✅ Usuario creado: ID $U1_ID"
    
    echo "2. Crear partido..."
    PARTIDO=$(curl -s -X POST "$BASE_URL/api/partidos" \
      -H "Content-Type: application/json" \
      -d "{
        \"organizadorId\": $U1_ID,
        \"tipoDeporte\": \"FUTBOL\",
        \"fechaHora\": \"2025-12-01T18:00:00\",
        \"direccion\": \"Cancha Test\",
        \"cantidadJugadoresRequeridos\": 10,
        \"nivelMinimoRequerido\": \"PRINCIPIANTE\",
        \"nivelMaximoRequerido\": \"AVANZADO\",
        \"duracionMinutos\": 90,
        \"descripcion\": \"Test\",
        \"latitud\": -34.60,
        \"longitud\": -58.38
      }")
    
    PID=$(echo "$PARTIDO" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
    echo "✅ Partido creado: ID $PID"
    echo "✅ Tests completados"
    ;;
    
  email)
    EMAIL=${2:-"test@example.com"}
    echo "📧 Enviando email a: $EMAIL"
    curl -X POST "$BASE_URL/api/test/email" \
      -H "Content-Type: application/json" \
      -d "{
        \"email\": \"$EMAIL\",
        \"asunto\": \"Test desde UnoMas\",
        \"mensaje\": \"Email de prueba funcionando correctamente\"
      }"
    echo ""
    ;;
    
  verify)
    echo "🔍 Verificando configuración..."
    
    if grep -q "^spring.mail.username=" src/main/resources/application.properties 2>/dev/null; then
      EMAIL=$(grep "^spring.mail.username=" src/main/resources/application.properties | cut -d'=' -f2)
      echo "✅ Email: $EMAIL"
    else
      echo "⚠️  Email no configurado"
    fi
    
    if [ -f "src/main/resources/firebase-service-account.json" ]; then
      echo "✅ Firebase configurado"
    else
      echo "⚠️  Firebase no configurado"
    fi
    
    if [ -f "target/unomas-backend-1.0.0.jar" ]; then
      echo "✅ JAR compilado"
    else
      echo "⚠️  Proyecto no compilado"
    fi
    ;;
    
  *)
    echo "UnoMas Backend - Script de gestión"
    echo ""
    echo "Uso: ./run.sh [comando] [opciones]"
    echo ""
    echo "Comandos:"
    echo "  start          Compila e inicia el backend"
    echo "  test           Ejecuta tests de la API"
    echo "  email [email]  Envía email de prueba"
    echo "  verify         Verifica la configuración"
    echo ""
    echo "Ejemplos:"
    echo "  ./run.sh start"
    echo "  ./run.sh test"
    echo "  ./run.sh email tu@email.com"
    echo "  ./run.sh verify"
    ;;
esac
