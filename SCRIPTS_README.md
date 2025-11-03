# Scripts del Proyecto Uno Mas

Este directorio contiene scripts para facilitar la gestión del proyecto.

## 📜 Scripts Disponibles

### 1. `check-requirements.sh` ✅
**Propósito:** Verificar que todos los requisitos del sistema estén instalados.

**Uso:**
```bash
./check-requirements.sh
```

**Verifica:**
- Java 17 o superior
- Maven Wrapper
- Estructura del proyecto
- Herramientas opcionales (curl, jq, git)

**Compatible con:** macOS, Linux, Windows (Git Bash)

---

### 2. `manage.sh` 🎛️
**Propósito:** Script de gestión completa con menú interactivo.

**Uso:**
```bash
./manage.sh
```

**Funcionalidades:**
- **Compilación:** Compilar proyecto con/sin tests, limpiar
- **Ejecución:** Iniciar/detener/reiniciar backend
- **Información:** Ver estado, logs en tiempo real
- **Testing:** Ejecutar tests, probar API con curl
- **Utilidades:** Abrir en navegador, verificar requisitos

**Características:**
- ✅ Usa Maven Wrapper automáticamente
- ✅ Detecta si el backend está corriendo
- ✅ Gestión de procesos (PID file)
- ✅ Logs persistentes
- ✅ Menús interactivos con bucles
- ✅ Colores para mejor visualización

**Compatibilidad:** macOS, Linux

---

### 3. `quick-start.sh` 🚀
**Propósito:** Inicio rápido del backend con compilación automática.

**Uso:**
```bash
./quick-start.sh
```

**Hace:**
1. Verifica si el proyecto está compilado
2. Lo compila automáticamente si es necesario
3. Inicia el backend
4. Muestra las URLs disponibles

**Compatible con:** macOS, Linux

---

### 4. `mvnw` / `mvnw.cmd` 📦
**Propósito:** Maven Wrapper - Maven incluido en el proyecto.

**Uso:**

macOS/Linux:
```bash
./mvnw clean package
./mvnw spring-boot:run
./mvnw test
```

Windows CMD:
```cmd
mvnw.cmd clean package
mvnw.cmd spring-boot:run
mvnw.cmd test
```

Windows PowerShell:
```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
```

**Ventajas:**
- ✅ No requiere Maven instalado globalmente
- ✅ Versión consistente en todos los entornos
- ✅ Funciona en Windows, macOS y Linux
- ✅ Se descarga automáticamente si no existe

---

### 5. `test-api.sh` 🧪
**Propósito:** Script para probar todos los endpoints de la API.

**Uso:**
```bash
./test-api.sh
```

**Prueba:**
- Registro de usuarios
- Creación de partidos
- Búsqueda de partidos
- Unirse a partidos
- Flujo completo del sistema

**Requiere:** Backend ejecutándose, curl y jq instalados

---

### 6. `package.sh` 📦
**Propósito:** Compilar y empaquetar el proyecto.

**Uso:**
```bash
./package.sh
```

---

## 🎯 Flujos de Trabajo Recomendados

### Primera Vez - Setup Completo

```bash
# 1. Verificar requisitos
./check-requirements.sh

# 2. Compilar el proyecto
./mvnw clean package -DskipTests

# 3. Iniciar el backend
./quick-start.sh

# O usar el script de gestión
./manage.sh
# → Opción 1: Compilar
# → Opción 4: Iniciar backend
```

### Desarrollo Diario

```bash
# Opción 1: Script de gestión (recomendado)
./manage.sh
# → Menú interactivo con todas las opciones

# Opción 2: Comandos directos
./mvnw spring-boot:run  # Iniciar directamente sin compilar JAR
```

### Testing

```bash
# Ejecutar tests
./mvnw test

# O desde el script de gestión
./manage.sh
# → Opción 10: Ejecutar tests
# → Opción 11: Probar API
```

### Despliegue/Producción

```bash
# 1. Compilar con tests
./mvnw clean package

# 2. Ejecutar JAR
java -jar target/unomas-backend-1.0.0.jar
```

## 🔧 Personalización

### Modificar Puerto del Servidor

Editar `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Cambiar Configuración de Base de Datos

Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:unomas
```

## 📊 Estructura de Archivos

```
.
├── check-requirements.sh   # Verificación de requisitos
├── manage.sh              # Script principal de gestión
├── quick-start.sh         # Inicio rápido
├── test-api.sh            # Pruebas de API
├── package.sh             # Empaquetado
├── mvnw                   # Maven Wrapper (Unix)
├── mvnw.cmd               # Maven Wrapper (Windows)
├── .mvn/                  # Configuración Maven Wrapper
│   └── wrapper/
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
└── backend.log            # Logs del backend (generado)
```

## 🐛 Solución de Problemas

### "Permission denied" al ejecutar scripts

```bash
chmod +x *.sh mvnw
```

### Maven Wrapper no funciona

```bash
# Regenerar Maven Wrapper
mvn wrapper:wrapper

# O dar permisos manualmente
chmod +x mvnw
```

### Puerto 8080 ocupado

```bash
# Detener proceso en puerto 8080 (macOS/Linux)
lsof -ti:8080 | xargs kill -9

# O cambiar puerto en application.properties
```

## 💡 Tips

1. **Usa `manage.sh`** para la mayoría de operaciones - es el más completo
2. **Usa `quick-start.sh`** para inicio rápido sin menús
3. **Usa `./mvnw`** directamente para comandos específicos de Maven
4. **Verifica requisitos** antes de reportar problemas: `./check-requirements.sh`

## 📝 Comandos Maven Wrapper Útiles

```bash
# Ver versión
./mvnw -v

# Limpiar proyecto
./mvnw clean

# Compilar
./mvnw compile

# Compilar y empaquetar
./mvnw package

# Compilar sin tests
./mvnw package -DskipTests

# Solo tests
./mvnw test

# Ejecutar aplicación
./mvnw spring-boot:run

# Ver dependencias
./mvnw dependency:tree

# Actualizar dependencias
./mvnw versions:display-dependency-updates
```

---

Para más información, consulta:
- `QUICK_START.md` - Guía de inicio rápido multiplataforma
- `README.md` - Documentación general del proyecto
- `INICIO_RAPIDO.md` - Guía en español
