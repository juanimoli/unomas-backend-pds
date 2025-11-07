#!/bin/bash

# Script para compilar la app Android nativa

set -e

echo "🏗️  Compilando aplicación Android Uno Más..."
echo ""

cd "$(dirname "$0")"

# Verificar que google-services.json existe
if [ ! -f "app/google-services.json" ]; then
    echo "❌ Error: No se encontró app/google-services.json"
    echo "   Copia el archivo desde mobile-app/google-services.json"
    exit 1
fi

echo "✅ google-services.json encontrado"
echo ""

# Limpiar builds anteriores
echo "🧹 Limpiando builds anteriores..."
./gradlew clean

echo ""
echo "📦 Compilando APK de debug..."
./gradlew assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo ""
    echo "✅ ¡Compilación exitosa!"
    echo ""
    echo "📱 APK generado:"
    echo "   Ubicación: $APK_PATH"
    echo "   Tamaño: $APK_SIZE"
    echo ""
    echo "🚀 Para instalar en dispositivo/emulador:"
    echo "   adb install $APK_PATH"
    echo ""
    echo "▶️  Para ejecutar:"
    echo "   adb shell am start -n com.unomas.mobile/.MainActivity"
else
    echo ""
    echo "❌ Error: No se pudo generar el APK"
    exit 1
fi
