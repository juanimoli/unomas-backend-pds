#!/bin/bash

# Script de inicio rápido para el backend

echo "🚀 Iniciando Backend Uno Mas..."
echo ""

# Asegurar que Homebrew esté en el PATH
if [ -d "/opt/homebrew/bin" ] && [[ ":$PATH:" != *":/opt/homebrew/bin:"* ]]; then
    export PATH="/opt/homebrew/bin:$PATH"
fi

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$PROJECT_DIR/target/unomas-backend-1.0.0.jar"
MVNW="$PROJECT_DIR/mvnw"

# Detectar Maven
if [ -x "$MVNW" ]; then
    MVN_CMD="$MVNW"
    echo "✓ Usando Maven Wrapper"
elif command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
    echo "✓ Usando Maven del sistema"
else
    echo "❌ Maven no está disponible"
    echo ""
    echo "Por favor ejecuta primero:"
    echo "  ./check-requirements.sh"
    exit 1
fi

echo ""

# Verificar si el JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "⚠️  El proyecto no está compilado. Compilando..."
    echo ""
    cd "$PROJECT_DIR"
    "$MVN_CMD" clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        echo ""
        echo "❌ Error al compilar el proyecto"
        exit 1
    fi
fi

# Iniciar el backend
echo ""
echo "✅ Iniciando servidor Spring Boot..."
echo ""
echo "📍 URLs disponibles:"
echo "   • API REST:    http://localhost:8080"
echo "   • Swagger UI:  http://localhost:8080/swagger-ui.html"
echo "   • H2 Console:  http://localhost:8080/h2-console"
echo ""
echo "⏹  Presiona Ctrl+C para detener el servidor"
echo ""
echo "─────────────────────────────────────────────────────"
echo ""

java -jar "$JAR_FILE"
