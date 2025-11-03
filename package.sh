#!/bin/bash

# Script para empaquetar el proyecto para entrega
# Uso: ./package.sh

echo "=========================================="
echo "Empaquetando proyecto Uno Mas"
echo "=========================================="

# Nombre del archivo de salida
OUTPUT_FILE="uno-mas-tp-adoo-entrega.zip"

# Limpiar y compilar el proyecto
echo ""
echo "1. Limpiando y compilando proyecto..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: La compilación falló"
    exit 1
fi

echo "✓ Compilación exitosa"

# Crear directorio temporal
TEMP_DIR="uno-mas-entrega"
rm -rf $TEMP_DIR
mkdir -p $TEMP_DIR

echo ""
echo "2. Copiando archivos..."

# Copiar código fuente
cp -r src $TEMP_DIR/
echo "✓ Código fuente copiado"

# Copiar archivos de configuración
cp pom.xml $TEMP_DIR/
cp -r .mvn $TEMP_DIR/ 2>/dev/null || true
echo "✓ Configuración copiada"

# Copiar documentación
cp README.md $TEMP_DIR/
cp PATRONES.md $TEMP_DIR/
cp EJEMPLOS_USO.md $TEMP_DIR/
cp RESUMEN_PROYECTO.md $TEMP_DIR/
echo "✓ Documentación copiada"

# Copiar .gitignore
cp .gitignore $TEMP_DIR/

# Crear archivo JAR ejecutable
echo ""
echo "3. Copiando JAR ejecutable..."
mkdir -p $TEMP_DIR/target
cp target/*.jar $TEMP_DIR/target/ 2>/dev/null || echo "⚠ No se encontró JAR (ejecutar mvn package primero)"

# Crear archivo de instrucciones
cat > $TEMP_DIR/INSTRUCCIONES.txt << 'EOF'
===========================================
UNO MAS - INSTRUCCIONES DE EJECUCIÓN
===========================================

PREREQUISITOS:
- Java 17 o superior
- Maven 3.6 o superior

OPCIÓN 1 - Ejecutar JAR (Recomendado):
--------------------------------------
cd target
java -jar unomas-backend-1.0.0.jar

La aplicación iniciará en: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

OPCIÓN 2 - Compilar y ejecutar con Maven:
-----------------------------------------
mvn clean install
mvn spring-boot:run

ARCHIVOS IMPORTANTES:
--------------------
- README.md: Información general del proyecto
- PATRONES.md: Explicación detallada de patrones de diseño
- EJEMPLOS_USO.md: Ejemplos de uso de la API con cURL
- RESUMEN_PROYECTO.md: Resumen ejecutivo del proyecto

ESTRUCTURA DEL CÓDIGO:
---------------------
src/main/java/com/unomas/
  ├── adapter/       # Patrón Adapter
  ├── controller/    # Controllers (MVC)
  ├── factory/       # Patrón Factory
  ├── model/         # Entidades (MVC)
  ├── observer/      # Patrón Observer
  ├── service/       # Services (MVC)
  ├── state/         # Patrón State
  └── strategy/      # Patrón Strategy

VERIFICAR FUNCIONAMIENTO:
------------------------
1. Iniciar aplicación
2. Abrir: http://localhost:8080/swagger-ui.html
3. Probar endpoint: POST /api/usuarios/registro
4. Crear usuario y partido
5. Verificar logs de notificaciones

CONTACTO:
--------
Trabajo Práctico - ADOO
Sistema Uno Mas
2025
===========================================
EOF

echo "✓ Instrucciones creadas"

# Crear archivo ZIP
echo ""
echo "4. Creando archivo ZIP..."
rm -f $OUTPUT_FILE
zip -r $OUTPUT_FILE $TEMP_DIR -x "*/target/*" "*/.*" > /dev/null

if [ $? -eq 0 ]; then
    echo "✓ Archivo ZIP creado: $OUTPUT_FILE"
else
    echo "✗ Error al crear ZIP"
    exit 1
fi

# Limpiar directorio temporal
rm -rf $TEMP_DIR

# Mostrar tamaño del archivo
FILE_SIZE=$(du -h $OUTPUT_FILE | cut -f1)

echo ""
echo "=========================================="
echo "✓ EMPAQUETADO COMPLETO"
echo "=========================================="
echo "Archivo: $OUTPUT_FILE"
echo "Tamaño: $FILE_SIZE"
echo ""
echo "Contenido del paquete:"
echo "  - Código fuente completo"
echo "  - Archivos de configuración"
echo "  - Documentación detallada"
echo "  - Instrucciones de ejecución"
echo ""
echo "El archivo está listo para entrega."
echo "=========================================="
