#!/bin/bash

# ========================================
# Verificación de Requisitos del Sistema
# Sistema Uno Mas - Compatible Windows/Mac/Linux
# ========================================

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

# Detectar sistema operativo
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macOS"
    elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
        echo "Windows"
    else
        echo "Unknown"
    fi
}

OS=$(detect_os)

echo "╔═══════════════════════════════════════════════════════╗"
echo "║                                                       ║"
echo "║     Sistema Uno Mas - Verificación de Requisitos     ║"
echo "║                                                       ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""
echo "Sistema Operativo Detectado: $OS"
echo ""

# Verificar Java
echo "════════════════════════════════════════════════════════"
echo "1. Verificando Java..."
echo "════════════════════════════════════════════════════════"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
    
    if [ "$JAVA_MAJOR_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION encontrado (requerido: 17+)"
        java -version 2>&1 | head -n 3 | sed 's/^/  /'
    else
        print_error "Java $JAVA_VERSION encontrado, pero se requiere Java 17+"
        echo ""
        echo "  Por favor instala Java 17 o superior:"
        case $OS in
            "macOS")
                echo "    brew install openjdk@17"
                echo "    sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk"
                ;;
            "Linux")
                echo "    sudo apt-get install openjdk-17-jdk  # Ubuntu/Debian"
                echo "    sudo yum install java-17-openjdk     # RHEL/CentOS"
                ;;
            "Windows")
                echo "    Descarga desde: https://adoptium.net/"
                echo "    O usando Chocolatey: choco install openjdk17"
                ;;
        esac
        exit 1
    fi
else
    print_error "Java no está instalado"
    echo ""
    echo "  Por favor instala Java 17 o superior:"
    case $OS in
        "macOS")
            echo "    brew install openjdk@17"
            ;;
        "Linux")
            echo "    sudo apt-get install openjdk-17-jdk  # Ubuntu/Debian"
            echo "    sudo yum install java-17-openjdk     # RHEL/CentOS"
            ;;
        "Windows")
            echo "    Descarga desde: https://adoptium.net/"
            echo "    O usando Chocolatey: choco install openjdk17"
            ;;
    esac
    exit 1
fi

echo ""

# Verificar Maven Wrapper
echo "════════════════════════════════════════════════════════"
echo "2. Verificando Maven Wrapper..."
echo "════════════════════════════════════════════════════════"

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ "$OS" == "Windows" ]]; then
    MVNW="$PROJECT_DIR/mvnw.cmd"
else
    MVNW="$PROJECT_DIR/mvnw"
fi

if [ -f "$MVNW" ]; then
    print_success "Maven Wrapper encontrado: $MVNW"
    if [ -x "$MVNW" ]; then
        print_success "Maven Wrapper tiene permisos de ejecución"
    else
        print_warning "Agregando permisos de ejecución a Maven Wrapper..."
        chmod +x "$MVNW"
        print_success "Permisos de ejecución agregados"
    fi
    
    # Verificar versión de Maven
    echo ""
    print_info "Verificando versión de Maven..."
    "$MVNW" -v 2>&1 | head -n 1 | sed 's/^/  /'
else
    print_error "Maven Wrapper no encontrado"
    echo ""
    echo "  Generando Maven Wrapper..."
    if command -v mvn &> /dev/null; then
        mvn wrapper:wrapper
        print_success "Maven Wrapper generado correctamente"
    else
        print_error "Maven no está instalado y no se puede generar el wrapper"
        echo ""
        echo "  Por favor instala Maven manualmente:"
        case $OS in
            "macOS")
                echo "    brew install maven"
                ;;
            "Linux")
                echo "    sudo apt-get install maven  # Ubuntu/Debian"
                echo "    sudo yum install maven       # RHEL/CentOS"
                ;;
            "Windows")
                echo "    Descarga desde: https://maven.apache.org/download.cgi"
                echo "    O usando Chocolatey: choco install maven"
                ;;
        esac
        exit 1
    fi
fi

echo ""

# Verificar estructura del proyecto
echo "════════════════════════════════════════════════════════"
echo "3. Verificando estructura del proyecto..."
echo "════════════════════════════════════════════════════════"

REQUIRED_FILES=(
    "pom.xml"
    "src/main/java/com/unomas/UnoMasApplication.java"
    "src/main/resources/application.properties"
)

MISSING_FILES=0
for file in "${REQUIRED_FILES[@]}"; do
    if [ -f "$PROJECT_DIR/$file" ]; then
        print_success "$file"
    else
        print_error "$file no encontrado"
        MISSING_FILES=$((MISSING_FILES + 1))
    fi
done

if [ $MISSING_FILES -gt 0 ]; then
    echo ""
    print_error "Faltan $MISSING_FILES archivos del proyecto"
    exit 1
fi

echo ""

# Verificar herramientas opcionales
echo "════════════════════════════════════════════════════════"
echo "4. Verificando herramientas opcionales..."
echo "════════════════════════════════════════════════════════"

# curl
if command -v curl &> /dev/null; then
    print_success "curl instalado (útil para probar la API)"
else
    print_warning "curl no está instalado (recomendado para pruebas de API)"
    case $OS in
        "macOS")
            echo "  Instalar: brew install curl"
            ;;
        "Linux")
            echo "  Instalar: sudo apt-get install curl"
            ;;
        "Windows")
            echo "  curl viene incluido en Windows 10+"
            ;;
    esac
fi

# jq
if command -v jq &> /dev/null; then
    print_success "jq instalado (útil para formatear JSON)"
else
    print_warning "jq no está instalado (recomendado para formatear respuestas JSON)"
    case $OS in
        "macOS")
            echo "  Instalar: brew install jq"
            ;;
        "Linux")
            echo "  Instalar: sudo apt-get install jq"
            ;;
        "Windows")
            echo "  Descargar desde: https://stedolan.github.io/jq/download/"
            ;;
    esac
fi

# git
if command -v git &> /dev/null; then
    print_success "git instalado"
else
    print_warning "git no está instalado (recomendado para control de versiones)"
fi

echo ""
echo "════════════════════════════════════════════════════════"
echo "✓ Verificación completada"
echo "════════════════════════════════════════════════════════"
echo ""
print_success "Todos los requisitos obligatorios están instalados"
echo ""
echo "Puedes iniciar el proyecto con:"
echo "  ./mvnw spring-boot:run           # Iniciar directamente"
echo "  ./mvnw clean package             # Compilar"
echo "  java -jar target/*.jar           # Ejecutar JAR compilado"
echo ""

if [[ "$OS" == "Windows" ]]; then
    echo "En Windows PowerShell usa:"
    echo "  .\\mvnw.cmd spring-boot:run"
    echo "  .\\mvnw.cmd clean package"
    echo ""
fi
