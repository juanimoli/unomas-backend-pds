#!/bin/bash

# ========================================
# Sistema Uno Mas - Script de Gestión
# ========================================

# Asegurar que Homebrew esté en el PATH
if [ -d "/opt/homebrew/bin" ] && [[ ":$PATH:" != *":/opt/homebrew/bin:"* ]]; then
    export PATH="/opt/homebrew/bin:$PATH"
fi

# Detectar Maven Wrapper
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MVNW="$PROJECT_DIR/mvnw"

# Usar Maven Wrapper si existe, sino usar mvn del sistema
if [ -x "$MVNW" ]; then
    MVN_CMD="$MVNW"
    MVN_TYPE="Maven Wrapper"
elif command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
    MVN_TYPE="Maven del sistema"
else
    MVN_CMD=""
    MVN_TYPE="No disponible"
fi

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Variables
TARGET_DIR="$PROJECT_DIR/target"
JAR_FILE="$TARGET_DIR/unomas-backend-1.0.0.jar"
PID_FILE="$PROJECT_DIR/.backend.pid"
LOG_FILE="$PROJECT_DIR/backend.log"

# ========================================
# Funciones Auxiliares
# ========================================

print_header() {
    clear
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║                                                       ║${NC}"
    echo -e "${CYAN}║           ${PURPLE}Sistema Uno Mas - Gestión Backend${CYAN}           ║${NC}"
    echo -e "${CYAN}║                                                       ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
}

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

check_requirements() {
    local missing_requirements=0
    
    if ! command -v java &> /dev/null; then
        print_error "Java no está instalado"
        missing_requirements=1
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d'.' -f1)
        
        if [ "$JAVA_MAJOR_VERSION" -ge 17 ]; then
            print_success "Java $JAVA_VERSION encontrado"
        else
            print_error "Java $JAVA_VERSION encontrado, pero se requiere Java 17+"
            missing_requirements=1
        fi
    fi
    
    if [ -z "$MVN_CMD" ]; then
        print_error "Maven no está disponible"
        missing_requirements=1
    else
        print_success "$MVN_TYPE encontrado"
    fi
    
    if [ $missing_requirements -eq 1 ]; then
        echo ""
        print_error "Faltan requisitos. Ejecuta el script de verificación:"
        echo "  ./check-requirements.sh"
        echo ""
        print_info "O instala manualmente:"
        echo "  - Java 17+: brew install openjdk@17"
        echo "  - Maven Wrapper está incluido en el proyecto"
        echo ""
        print_info "Después de instalar, recarga el terminal:"
        echo "  source ~/.zshrc"
        return 1
    fi
    
    return 0
}

check_maven() {
    if [ -z "$MVN_CMD" ]; then
        print_error "Maven no está disponible"
        echo ""
        print_info "Maven Wrapper debería estar incluido en el proyecto."
        print_info "Verifica los requisitos ejecutando:"
        echo "  ./check-requirements.sh"
        echo ""
        read -p "Presiona Enter para continuar..."
        return 1
    fi
    return 0
}

is_compiled() {
    if [ -f "$JAR_FILE" ]; then
        return 0
    else
        return 1
    fi
}

is_running() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if ps -p $pid > /dev/null 2>&1; then
            return 0
        else
            rm -f "$PID_FILE"
            return 1
        fi
    fi
    return 1
}

get_backend_status() {
    if is_running; then
        local pid=$(cat "$PID_FILE")
        echo -e "${GREEN}● EJECUTANDO${NC} (PID: $pid)"
    else
        echo -e "${RED}● DETENIDO${NC}"
    fi
}

# ========================================
# Funciones de Compilación
# ========================================

compile_project() {
    print_header
    echo -e "${BLUE}═══ Compilando Proyecto ═══${NC}"
    echo ""
    
    if ! check_maven; then
        return 1
    fi
    
    print_info "Usando: $MVN_TYPE"
    print_info "Limpiando compilaciones previas..."
    "$MVN_CMD" clean
    
    echo ""
    print_info "Compilando proyecto (omitiendo tests)..."
    "$MVN_CMD" package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo ""
        print_success "Proyecto compilado exitosamente"
        print_info "JAR generado: $JAR_FILE"
        return 0
    else
        echo ""
        print_error "Error al compilar el proyecto"
        return 1
    fi
}

compile_with_tests() {
    print_header
    echo -e "${BLUE}═══ Compilando con Tests ═══${NC}"
    echo ""
    
    if ! check_maven; then
        return 1
    fi
    
    print_info "Usando: $MVN_TYPE"
    print_info "Limpiando compilaciones previas..."
    "$MVN_CMD" clean
    
    echo ""
    print_info "Compilando y ejecutando tests..."
    "$MVN_CMD" package
    
    if [ $? -eq 0 ]; then
        echo ""
        print_success "Proyecto compilado y tests ejecutados exitosamente"
        print_info "JAR generado: $JAR_FILE"
        return 0
    else
        echo ""
        print_error "Error al compilar o ejecutar tests"
        return 1
    fi
}

clean_project() {
    print_header
    echo -e "${BLUE}═══ Limpiando Proyecto ═══${NC}"
    echo ""
    
    if is_running; then
        print_warning "El backend está ejecutándose. Deteniéndolo primero..."
        stop_backend
        sleep 2
    fi
    
    if [ -n "$MVN_CMD" ]; then
        print_info "Eliminando archivos compilados con $MVN_TYPE..."
        "$MVN_CMD" clean
    else
        print_info "Maven no disponible, eliminando manualmente..."
        if [ -d "$TARGET_DIR" ]; then
            rm -rf "$TARGET_DIR"
            print_success "Directorio target eliminado"
        fi
    fi
    
    if [ -f "$PID_FILE" ]; then
        rm -f "$PID_FILE"
        print_success "Archivo PID eliminado"
    fi
    
    if [ -f "$LOG_FILE" ]; then
        rm -f "$LOG_FILE"
        print_success "Archivo de log eliminado"
    fi
    
    print_success "Proyecto limpio"
}

# ========================================
# Funciones de Ejecución
# ========================================

start_backend() {
    print_header
    echo -e "${BLUE}═══ Iniciando Backend ═══${NC}"
    echo ""
    
    if is_running; then
        print_warning "El backend ya está ejecutándose (PID: $(cat $PID_FILE))"
        return 1
    fi
    
    if ! is_compiled; then
        print_warning "El proyecto no está compilado. Compilando primero..."
        compile_project
        if [ $? -ne 0 ]; then
            return 1
        fi
    fi
    
    print_info "Iniciando servidor Spring Boot..."
    nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
    local pid=$!
    echo $pid > "$PID_FILE"
    
    print_info "Esperando a que el servidor inicie..."
    sleep 5
    
    if is_running; then
        print_success "Backend iniciado exitosamente (PID: $pid)"
        print_info "API disponible en: http://localhost:8080"
        print_info "Swagger UI: http://localhost:8080/swagger-ui.html"
        print_info "H2 Console: http://localhost:8080/h2-console"
        print_info "Logs: $LOG_FILE"
        return 0
    else
        print_error "Error al iniciar el backend"
        cat "$LOG_FILE"
        return 1
    fi
}

stop_backend() {
    print_header
    echo -e "${BLUE}═══ Deteniendo Backend ═══${NC}"
    echo ""
    
    if ! is_running; then
        print_warning "El backend no está ejecutándose"
        return 1
    fi
    
    local pid=$(cat "$PID_FILE")
    print_info "Deteniendo servidor (PID: $pid)..."
    
    kill $pid
    sleep 2
    
    if ps -p $pid > /dev/null 2>&1; then
        print_warning "Forzando detención..."
        kill -9 $pid
        sleep 1
    fi
    
    rm -f "$PID_FILE"
    print_success "Backend detenido"
}

restart_backend() {
    print_header
    echo -e "${BLUE}═══ Reiniciando Backend ═══${NC}"
    echo ""
    
    if is_running; then
        stop_backend
        sleep 2
    fi
    
    start_backend
}

start_with_logs() {
    start_backend
    if [ $? -eq 0 ]; then
        echo ""
        print_info "Mostrando logs en tiempo real (Ctrl+C para salir)..."
        echo ""
        tail -f "$LOG_FILE"
    fi
}

# ========================================
# Funciones de Información
# ========================================

show_status() {
    print_header
    echo -e "${BLUE}═══ Estado del Sistema ═══${NC}"
    echo ""
    
    echo -e "${CYAN}Backend:${NC} $(get_backend_status)"
    
    if is_compiled; then
        echo -e "${CYAN}Compilación:${NC} ${GREEN}✓ Compilado${NC}"
        echo -e "${CYAN}JAR:${NC} $JAR_FILE"
        echo -e "${CYAN}Tamaño:${NC} $(du -h "$JAR_FILE" | cut -f1)"
    else
        echo -e "${CYAN}Compilación:${NC} ${YELLOW}⚠ No compilado${NC}"
    fi
    
    if [ -f "$LOG_FILE" ]; then
        echo -e "${CYAN}Log:${NC} $LOG_FILE ($(wc -l < "$LOG_FILE") líneas)"
    fi
    
    echo ""
    echo -e "${CYAN}URLs del servicio:${NC}"
    echo "  • API REST: http://localhost:8080"
    echo "  • Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  • H2 Console: http://localhost:8080/h2-console"
    
    if is_running; then
        echo ""
        echo -e "${CYAN}Verificando conectividad...${NC}"
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "API respondiendo correctamente"
        else
            print_warning "API no responde (puede estar iniciando...)"
        fi
    fi
}

show_logs() {
    if [ ! -f "$LOG_FILE" ]; then
        print_error "No hay archivo de logs"
        echo ""
        read -p "Presiona Enter para continuar..."
        return 1
    fi
    
    while true; do
        print_header
        echo -e "${BLUE}═══ Logs del Backend ═══${NC}"
        echo ""
        
        echo "1. Ver todas las líneas"
        echo "2. Ver últimas 50 líneas"
        echo "3. Ver últimas 100 líneas"
        echo "4. Seguir logs en tiempo real"
        echo "5. Buscar en logs"
        echo "0. Volver al menú principal"
        echo ""
        
        read -p "Selecciona una opción: " log_option
        
        case $log_option in
            1)
                less "$LOG_FILE"
                ;;
            2)
                tail -n 50 "$LOG_FILE"
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            3)
                tail -n 100 "$LOG_FILE"
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            4)
                print_info "Mostrando logs en tiempo real (Ctrl+C para salir)..."
                tail -f "$LOG_FILE"
                ;;
            5)
                read -p "Ingresa texto a buscar: " search_text
                grep -i --color "$search_text" "$LOG_FILE" | less -R
                ;;
            0)
                return 0
                ;;
            *)
                print_error "Opción inválida"
                sleep 1
                ;;
        esac
    done
}

# ========================================
# Funciones de Testing
# ========================================

run_tests() {
    print_header
    echo -e "${BLUE}═══ Ejecutando Tests ═══${NC}"
    echo ""
    
    if ! check_maven; then
        return 1
    fi
    
    print_info "Usando: $MVN_TYPE"
    print_info "Ejecutando tests unitarios..."
    "$MVN_CMD" test
    
    if [ $? -eq 0 ]; then
        echo ""
        print_success "Tests ejecutados exitosamente"
    else
        echo ""
        print_error "Algunos tests fallaron"
    fi
}

test_api() {
    while true; do
        print_header
        echo -e "${BLUE}═══ Probando API ═══${NC}"
        echo ""
        
        if ! is_running; then
            print_error "El backend no está ejecutándose"
            print_info "Inicia el backend primero (opción 4)"
            echo ""
            read -p "Presiona Enter para volver..."
            return 1
        fi
        
        echo "1. Registrar usuario"
        echo "2. Listar usuarios"
        echo "3. Crear partido"
        echo "4. Buscar partidos"
        echo "5. Unirse a partido"
        echo "6. Ver estado del sistema"
        echo "7. Ejecutar script de pruebas completo"
        echo "0. Volver al menú principal"
        echo ""
        
        read -p "Selecciona una opción: " api_option
        
        case $api_option in
            1)
                echo ""
                print_info "Registrando usuario de prueba..."
                curl -X POST http://localhost:8080/api/usuarios \
                    -H "Content-Type: application/json" \
                    -d '{
                        "nombreUsuario": "test_user_'$(date +%s)'",
                        "email": "test'$(date +%s)'@example.com",
                        "contrasena": "password123",
                        "deporteFavorito": "FUTBOL",
                        "nivelJuego": "INTERMEDIO",
                        "ubicacion": "-34.6037,-58.3816",
                        "notificacionesEmail": true,
                        "notificacionesPush": false
                    }' | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            2)
                echo ""
                print_info "Obteniendo lista de usuarios..."
                curl -X GET http://localhost:8080/api/usuarios | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            3)
                echo ""
                print_info "Creando partido de prueba..."
                curl -X POST http://localhost:8080/api/partidos \
                    -H "Content-Type: application/json" \
                    -d '{
                        "tipoDeporte": "FUTBOL_5",
                        "ubicacion": "-34.6037,-58.3816",
                        "direccion": "Parque Centenario, Buenos Aires",
                        "fechaHora": "'$(date -u -v+1d +%Y-%m-%dT%H:%M:%S)'",
                        "organizadorId": 1,
                        "permiteCualquierNivel": true,
                        "descripcion": "Partido de prueba"
                    }' | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            4)
                echo ""
                print_info "Buscando partidos disponibles..."
                curl -X GET "http://localhost:8080/api/partidos/buscar?estado=NECESITAMOS_JUGADORES" | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            5)
                echo ""
                read -p "ID del partido: " partido_id
                read -p "ID del usuario: " usuario_id
                echo ""
                print_info "Uniendo usuario $usuario_id al partido $partido_id..."
                curl -X POST "http://localhost:8080/api/partidos/$partido_id/unirse" \
                    -H "Content-Type: application/json" \
                    -d "{\"usuarioId\": $usuario_id}" | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            6)
                echo ""
                print_info "Verificando estado del sistema..."
                curl -s http://localhost:8080/actuator/health | jq '.'
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            7)
                echo ""
                print_info "Ejecutando script de pruebas completo..."
                if [ -f "$PROJECT_DIR/test-api.sh" ]; then
                    bash "$PROJECT_DIR/test-api.sh"
                else
                    print_error "Script test-api.sh no encontrado"
                fi
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            0)
                return 0
                ;;
            *)
                print_error "Opción inválida"
                sleep 1
                ;;
        esac
    done
}

# ========================================
# Funciones de URLs
# ========================================

open_browser() {
    while true; do
        print_header
        echo -e "${BLUE}═══ Abrir en Navegador ═══${NC}"
        echo ""
        
        if ! is_running; then
            print_error "El backend no está ejecutándose"
            echo ""
            read -p "Presiona Enter para volver..."
            return 1
        fi
        
        echo "1. Swagger UI (Documentación API)"
        echo "2. H2 Console (Base de datos)"
        echo "3. API REST (JSON)"
        echo "0. Volver al menú principal"
        echo ""
        
        read -p "Selecciona una opción: " browser_option
        
        case $browser_option in
            1)
                print_info "Abriendo Swagger UI..."
                open "http://localhost:8080/swagger-ui.html"
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            2)
                print_info "Abriendo H2 Console..."
                echo ""
                print_info "Credenciales de acceso:"
                echo "  JDBC URL: jdbc:h2:mem:unomas"
                echo "  Username: sa"
                echo "  Password: (dejar vacío)"
                open "http://localhost:8080/h2-console"
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            3)
                print_info "Abriendo API REST..."
                open "http://localhost:8080/api/usuarios"
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            0)
                return 0
                ;;
            *)
                print_error "Opción inválida"
                sleep 1
                ;;
        esac
    done
}

# ========================================
# Menú Principal
# ========================================

show_menu() {
    print_header
    
    echo -e "${CYAN}Estado actual:${NC} $(get_backend_status)"
    echo ""
    
    if is_compiled; then
        echo -e "${GREEN}✓${NC} Proyecto compilado"
    else
        echo -e "${YELLOW}⚠${NC} Proyecto no compilado"
    fi
    
    echo ""
    echo -e "${CYAN}═══ Compilación ═══${NC}"
    echo "1. Compilar proyecto"
    echo "2. Compilar con tests"
    echo "3. Limpiar proyecto"
    echo ""
    
    echo -e "${CYAN}═══ Ejecución ═══${NC}"
    echo "4. Iniciar backend"
    echo "5. Iniciar backend con logs"
    echo "6. Detener backend"
    echo "7. Reiniciar backend"
    echo ""
    
    echo -e "${CYAN}═══ Información ═══${NC}"
    echo "8. Ver estado completo"
    echo "9. Ver logs"
    echo ""
    
    echo -e "${CYAN}═══ Testing ═══${NC}"
    echo "10. Ejecutar tests unitarios"
    echo "11. Probar API (curl)"
    echo ""
    
    echo -e "${CYAN}═══ Utilidades ═══${NC}"
    echo "12. Abrir en navegador"
    echo "13. Verificar requisitos"
    echo ""
    
    echo -e "${CYAN}0. Salir${NC}"
    echo ""
}

# ========================================
# Función Principal
# ========================================

main() {
    cd "$PROJECT_DIR"
    
    while true; do
        show_menu
        read -p "Selecciona una opción: " option
        
        case $option in
            1)
                compile_project
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            2)
                compile_with_tests
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            3)
                clean_project
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            4)
                start_backend
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            5)
                start_with_logs
                ;;
            6)
                stop_backend
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            7)
                restart_backend
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            8)
                show_status
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            9)
                show_logs
                ;;
            10)
                run_tests
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            11)
                test_api
                ;;
            12)
                open_browser
                ;;
            13)
                print_header
                echo -e "${BLUE}═══ Verificando Requisitos ═══${NC}"
                echo ""
                check_requirements
                echo ""
                read -p "Presiona Enter para continuar..."
                ;;
            0)
                print_header
                if is_running; then
                    echo ""
                    read -p "El backend está ejecutándose. ¿Deseas detenerlo antes de salir? (s/n): " stop_choice
                    if [ "$stop_choice" = "s" ]; then
                        stop_backend
                    fi
                fi
                echo ""
                print_info "¡Hasta pronto!"
                echo ""
                exit 0
                ;;
            *)
                print_error "Opción inválida"
                sleep 1
                ;;
        esac
    done
}

# ========================================
# Ejecutar
# ========================================

main
