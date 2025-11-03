# Guía de Instalación de Herramientas

## Instalación de Java 17

### macOS (usando Homebrew)
```bash
# Instalar Homebrew (si no lo tienes)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar Java 17
brew install openjdk@17

# Configurar JAVA_HOME
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc

# Verificar instalación
java -version
```

### Alternativa: Descargar desde Oracle
1. Ir a https://www.oracle.com/java/technologies/downloads/
2. Descargar Java 17 para macOS
3. Instalar el archivo .dmg
4. Configurar variables de entorno

---

## Instalación de Maven

### macOS (usando Homebrew)
```bash
# Instalar Maven
brew install maven

# Verificar instalación
mvn -version
```

### Alternativa: Instalación Manual
```bash
# Descargar Maven
curl -O https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz

# Extraer
tar -xzf apache-maven-3.9.5-bin.tar.gz

# Mover a /opt
sudo mv apache-maven-3.9.5 /opt/maven

# Configurar PATH
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# Verificar
mvn -version
```

---

## Instalación de Git (si es necesario)

### macOS
```bash
brew install git

# O usar el que viene con Xcode Command Line Tools
xcode-select --install
```

---

## Pasos para Ejecutar el Proyecto

### 1. Verificar Prerequisitos
```bash
java -version  # Debe mostrar versión 17 o superior
mvn -version   # Debe mostrar Maven 3.6 o superior
```

### 2. Compilar el Proyecto
```bash
cd /Users/juanimoli/Development/uno-mas-tp-adoo
mvn clean install
```

### 3. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

### 4. Verificar que Funciona
Abrir en el navegador:
- http://localhost:8080
- http://localhost:8080/swagger-ui.html

---

## Configuración del IDE (Opcional)

### IntelliJ IDEA
1. File → Open → Seleccionar carpeta del proyecto
2. IntelliJ detectará automáticamente el proyecto Maven
3. Esperar a que descargue dependencias
4. Ejecutar `UnoMasApplication.java`

### Visual Studio Code
1. Instalar extensiones:
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack
2. Abrir carpeta del proyecto
3. VS Code detectará el proyecto Maven
4. F5 para ejecutar

### Eclipse
1. File → Import → Maven → Existing Maven Projects
2. Seleccionar carpeta del proyecto
3. Wait for Maven dependencies
4. Run as → Spring Boot App

---

## Solución de Problemas Comunes

### Error: JAVA_HOME not set
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Error: Maven no encuentra Java
```bash
# Verificar JAVA_HOME
echo $JAVA_HOME

# Si está vacío, configurar
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Puerto 8080 ocupado
Cambiar puerto en `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Problemas de red al descargar dependencias
```bash
# Limpiar cache de Maven
mvn clean
rm -rf ~/.m2/repository

# Volver a descargar
mvn clean install
```

---

## Testing del Proyecto

```bash
# Ejecutar tests
mvn test

# Ejecutar con coverage
mvn clean test jacoco:report

# Ver reporte
open target/site/jacoco/index.html
```

---

## Empaquetar para Distribución

```bash
# Generar JAR ejecutable
mvn clean package

# El JAR estará en target/unomas-backend-1.0.0.jar

# Ejecutar JAR
java -jar target/unomas-backend-1.0.0.jar
```

---

## Recursos Adicionales

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Maven Tutorial: https://maven.apache.org/guides/
- Java SE 17 Docs: https://docs.oracle.com/en/java/javase/17/
- H2 Database: http://www.h2database.com/
