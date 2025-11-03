# Diagramas PlantUML - Sistema Uno Mas

Este directorio contiene todos los diagramas PlantUML del proyecto.

## 📋 Lista de Diagramas

### 1. Diagrama de Clases Completo (`complete-class-diagram.puml`)
**Descripción:** Diagrama de clases completo mostrando todos los patrones de diseño implementados.

**Incluye:**
- Todas las clases del sistema
- Controllers, Services, Repositories (MVC)
- Patrones Strategy, State, Observer, Factory, Adapter
- DTOs y Entities
- Relaciones entre componentes

### 2. Diagrama de Estados (`state-diagram.puml`)
**Descripción:** Diagrama de máquina de estados del partido.

**Incluye:**
- 6 estados del partido
- Transiciones entre estados
- Condiciones y eventos
- Estado inicial y finales

### 3. Diagrama de Secuencia (`sequence-diagram.puml`)
**Descripción:** Flujo completo de crear partido y unirse a él.

**Incluye:**
- Interacción entre actores y componentes
- Flujo de Factory Pattern
- Aplicación de Strategy Pattern
- Transiciones con State Pattern
- Notificaciones con Observer Pattern

### 4. Diagrama de Casos de Uso (`use-case-diagram.puml`)
**Descripción:** Casos de uso del sistema desde perspectiva del usuario.

**Incluye:**
- Actores: Usuario, Organizador, Jugador, Sistema
- Casos de uso principales
- Relaciones include y extend
- Procesos automáticos

### 5. Diagrama de Patrones (`patterns-overview.puml`)
**Descripción:** Vista general de todos los patrones implementados.

**Incluye:**
- Estructura de cada patrón
- Propósito y beneficios
- Relaciones entre patrones

### 6. Diagrama de Arquitectura (`architecture-diagram.puml`)
**Descripción:** Arquitectura de componentes del sistema.

**Incluye:**
- Capas de la aplicación
- Componentes principales
- Servicios externos
- Flujo de datos

## 🚀 Cómo Visualizar los Diagramas

### Opción 1: Online (PlantUML Server)
1. Ir a http://www.plantuml.com/plantuml/uml/
2. Copiar el contenido del archivo `.puml`
3. Pegar en el editor
4. Ver el diagrama generado

### Opción 2: VS Code
1. Instalar extensión "PlantUML" de jebbs
2. Instalar Graphviz: `brew install graphviz`
3. Abrir archivo `.puml`
4. Presionar `Alt+D` o `Cmd+D` para preview

### Opción 3: IntelliJ IDEA
1. Instalar plugin "PlantUML integration"
2. Abrir archivo `.puml`
3. El diagrama se mostrará automáticamente

### Opción 4: Línea de Comandos
```bash
# Instalar PlantUML
brew install plantuml

# Generar imagen PNG
plantuml complete-class-diagram.puml

# Generar SVG (mejor calidad)
plantuml -tsvg complete-class-diagram.puml

# Generar todos los diagramas
plantuml *.puml
```

## 📦 Exportar Diagramas

### Generar imágenes para la documentación:
```bash
# PNG (buena calidad)
plantuml -tpng *.puml

# SVG (mejor calidad, escalable)
plantuml -tsvg *.puml

# PDF (para documentos)
plantuml -tpdf *.puml
```

## 📚 Uso en Documento PDF

Para incluir en tu documento PDF del trabajo práctico:

1. **Generar imágenes de alta calidad:**
   ```bash
   plantuml -tsvg *.puml
   ```

2. **Importar en Word/Google Docs:**
   - Insertar → Imagen
   - Seleccionar archivo SVG o PNG generado

3. **Incluir en LaTeX:**
   ```latex
   \begin{figure}[h]
     \centering
     \includegraphics[width=\textwidth]{diagrams/complete-class-diagram.png}
     \caption{Diagrama de Clases Completo - Sistema Uno Mas}
   \end{figure}
   ```

## 🎨 Personalización

Todos los diagramas usan:
- Colores personalizados para cada patrón
- Notas explicativas
- Leyendas descriptivas
- Título y contexto

Puedes modificar:
- Colores: Editar `!define LIGHTBLUE #E3F2FD`
- Tamaño: Usar `scale 1.5` al inicio
- Dirección: `left to right direction` o `top to bottom direction`

## 📖 Documentación PlantUML

- Sitio oficial: https://plantuml.com/
- Guía de diagramas de clases: https://plantuml.com/class-diagram
- Guía de diagramas de secuencia: https://plantuml.com/sequence-diagram
- Guía de diagramas de estados: https://plantuml.com/state-diagram
- Guía de casos de uso: https://plantuml.com/use-case-diagram

## ✅ Checklist para la Entrega

- [ ] Generar todos los diagramas en formato PNG o SVG
- [ ] Incluir diagrama de clases completo en documento PDF
- [ ] Incluir diagrama de estados para explicar State Pattern
- [ ] Incluir diagrama de secuencia para mostrar flujo completo
- [ ] Agregar leyendas y explicaciones a cada diagrama
- [ ] Referenciar patrones de diseño en cada diagrama
- [ ] Exportar en alta resolución para impresión

## 🎯 Recomendaciones para la Presentación

1. **Comenzar con Architecture Diagram** - Da vista general
2. **Mostrar Complete Class Diagram** - Explica estructura
3. **Detallar cada Patrón** - Usar Patterns Overview
4. **Demostrar con Secuencia** - Muestra cómo todo funciona junto
5. **Explicar Estados** - State Diagram clarifica transiciones

---

_Para más información sobre el proyecto, ver README.md en el directorio raíz_
