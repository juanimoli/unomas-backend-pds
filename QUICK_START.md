# 🚀 Inicio Rápido - Sistema Uno Mas

Este documento explica cómo levantar el proyecto en **cualquier sistema operativo** (Windows, macOS, Linux).

## 📋 Requisitos

- **Java 17 o superior** (obligatorio)
- **Maven Wrapper** (incluido en el proyecto)

## ✅ Verificar Requisitos

### En macOS/Linux:

```bash
./check-requirements.sh
```

### En Windows PowerShell:

```powershell
bash check-requirements.sh
```

O manualmente:

```bash
java -version  # Debe ser 17+
./mvnw -v      # Windows: .\mvnw.cmd -v
```

## 📦 Instalación de Java

### macOS:

```bash
brew install openjdk@17
```

### Linux (Ubuntu/Debian):

```bash
sudo apt-get update
sudo apt-get install openjdk-17-jdk
```

### Linux (RHEL/CentOS):

```bash
sudo yum install java-17-openjdk
```

### Windows:

1. **Opción 1 - Usando Chocolatey:**
   ```powershell
   choco install openjdk17
   ```

2. **Opción 2 - Descarga manual:**
   - Descargar desde: https://adoptium.net/
   - Instalar y agregar a las variables de entorno

## 🎯 Compilar el Proyecto

### macOS/Linux:

```bash
./mvnw clean package -DskipTests
```

### Windows CMD:

```cmd
mvnw.cmd clean package -DskipTests
```

### Windows PowerShell:

```powershell
.\mvnw.cmd clean package -DskipTests
```

## ▶️ Iniciar el Backend

### Opción 1: Usando Maven Wrapper (Recomendado)

#### macOS/Linux:
```bash
./mvnw spring-boot:run
```

#### Windows CMD:
```cmd
mvnw.cmd spring-boot:run
```

#### Windows PowerShell:
```powershell
.\mvnw.cmd spring-boot:run
```

### Opción 2: Usando el JAR compilado

Primero compila el proyecto (ver sección anterior), luego:

```bash
java -jar target/unomas-backend-1.0.0.jar
```

Esto funciona igual en **Windows, macOS y Linux**.

### Opción 3: Script de inicio rápido (macOS/Linux)

```bash
./quick-start.sh
```

Este script:
- Verifica si el proyecto está compilado
- Lo compila si es necesario
- Inicia el servidor automáticamente

## 🌐 URLs del Sistema

Una vez iniciado, el backend estará disponible en:

- **API REST:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

### Credenciales H2 Console:

- **JDBC URL:** `jdbc:h2:mem:unomas`
- **Username:** `sa`
- **Password:** _(dejar vacío)_

## 🛠️ Scripts Disponibles (macOS/Linux)

### Script de Gestión Completa:

```bash
./manage.sh
```

Menú interactivo con opciones para:
- Compilar proyecto
- Iniciar/detener/reiniciar backend
- Ver logs
- Ejecutar tests
- Probar API con curl
- Abrir en navegador

### Verificación de Requisitos:

```bash
./check-requirements.sh
```

Verifica que todos los requisitos estén instalados.

## 🧪 Ejecutar Tests

### macOS/Linux:

```bash
./mvnw test
```

### Windows CMD:

```cmd
mvnw.cmd test
```

### Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## 🧹 Limpiar Proyecto

Para eliminar archivos compilados:

### macOS/Linux:

```bash
./mvnw clean
```

### Windows CMD:

```cmd
mvnw.cmd clean
```

### Windows PowerShell:

```powershell
.\mvnw.cmd clean
```

## 🐛 Solución de Problemas

### Error: "mvnw: Permission denied" (macOS/Linux)

```bash
chmod +x mvnw
```

### Error: "Java version is too old"

Asegúrate de tener Java 17+:

```bash
java -version
```

Si es menor, instala Java 17 (ver sección de instalación arriba).

### Error: "Port 8080 already in use"

El puerto 8080 está ocupado. Opciones:

1. **Detener el proceso que usa el puerto:**
   
   macOS/Linux:
   ```bash
   lsof -ti:8080 | xargs kill -9
   ```
   
   Windows PowerShell:
   ```powershell
   Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process
   ```

2. **Cambiar el puerto en `application.properties`:**
   ```properties
   server.port=8081
   ```

### Error: "JAVA_HOME not set" (Windows)

Agregar JAVA_HOME a las variables de entorno:

1. Panel de Control → Sistema → Configuración avanzada
2. Variables de entorno
3. Nueva variable del sistema:
   - Nombre: `JAVA_HOME`
   - Valor: `C:\Program Files\Java\jdk-17` (o tu ruta de instalación)
4. Agregar a Path: `%JAVA_HOME%\bin`

## 📝 Comandos Maven Útiles

### Compilar sin tests:

```bash
./mvnw clean package -DskipTests
```

### Compilar con tests:

```bash
./mvnw clean package
```

### Solo ejecutar tests:

```bash
./mvnw test
```

### Limpiar y reinstalar dependencias:

```bash
./mvnw clean install
```

### Ver versión de Maven:

```bash
./mvnw -v
```

### Ejecutar directamente sin compilar JAR:

```bash
./mvnw spring-boot:run
```

## 🔍 Verificar que Todo Funciona

Una vez iniciado el backend, puedes verificar que funciona:

### Usando curl (disponible en todos los sistemas):

```bash
curl http://localhost:8080/actuator/health
```

Deberías ver:
```json
{"status":"UP"}
```

### Usando un navegador:

Abre: http://localhost:8080/swagger-ui.html

Deberías ver la documentación interactiva de la API.

## 💡 Recomendaciones

1. **Usa Maven Wrapper** (`./mvnw` o `mvnw.cmd`) en lugar de Maven instalado globalmente
2. **Verifica Java 17+** antes de compilar
3. **Usa Swagger UI** para explorar y probar los endpoints
4. **Revisa los logs** si algo no funciona como esperas

## 🆘 ¿Necesitas Ayuda?

Si tienes problemas:

1. Ejecuta `./check-requirements.sh` para verificar dependencias
2. Revisa la sección de "Solución de Problemas" arriba
3. Verifica que el puerto 8080 esté disponible
4. Revisa los logs en la consola o en `backend.log`

---

**¡Listo!** Tu backend debería estar funcionando en cualquier sistema operativo. 🎉
