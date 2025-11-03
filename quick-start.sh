#!/bin/bash

# Script de inicio rápido para el backend

echo "🚀 Iniciando Backend Uno Mas..."
echo ""

# Asegurar que Homebrew esté en el PATH
if [ -d "/opt/homebrew/bin" ] && [[ ":$PATH:" != *":/opt/homebrew/bin:"* ]]; then
    export PATH="/opt/homebrew/bin:$PATH"
fi

JAR_FILE="/Users/juanimoli/Development/uno-mas-tp-adoo/target/unomas-backend-1.0.0.jar"

# Verificar si el JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo "⚠️  El proyecto no está compilado. Compilando..."
    echo ""
    cd /Users/juanimoli/Development/uno-mas-tp-adoo
    mvn clean package -DskipTests
    
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
