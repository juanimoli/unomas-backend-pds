# Patrones de Diseño Implementados

## 1. MVC (Model-View-Controller)

**Propósito**: Separar responsabilidades en capas

**Implementación**:
- **Model**: `Usuario`, `Partido` (`com.unomas.model`)
- **Controller**: `UsuarioController`, `PartidoController` (`com.unomas.controller`)
- **Service**: `UsuarioService`, `PartidoService` (`com.unomas.service`)

---

## 2. Strategy

**Propósito**: Algoritmos de emparejamiento intercambiables

**Interface**: `EmparejamientoStrategy`

**Estrategias**:
- `NivelHabilidadStrategy` - Por nivel de juego
- `CercaniaStrategy` - Por distancia geográfica  
- `HistorialStrategy` - Por historial previo

**Uso**:
```java
EmparejamientoStrategy strategy = new NivelHabilidadStrategy();
boolean compatible = strategy.esCompatible(usuario, partido);
```

---

## 3. State

**Propósito**: Gestionar transiciones de estado del partido

**Interface**: `EstadoPartido`

**Estados**:
1. `NecesitamosJugadoresState` → 2. `PartidoArmadoState` → 3. `ConfirmadoState` → 4. `EnJuegoState` → 5. `FinalizadoState`
- `CanceladoState` (desde cualquier estado pre-inicio)

**Transiciones automáticas**:
- NECESITAMOS → ARMADO (al completar equipo)
- CONFIRMADO → EN_JUEGO (por fecha/hora)

---

## 4. Observer

**Propósito**: Notificar cambios de estado

**Componentes**:
- `PartidoObservable` (Subject)
- `NotificacionObserver` (Interface)
- `EmailNotificationObserver`
- `PushNotificationObserver`

**Eventos notificados**: Cambios de estado, nuevo jugador, cancelaciones

---

## 5. Factory

**Propósito**: Crear partidos con diferentes configuraciones

**Clase**: `PartidoFactory`

**Métodos**:
- `crearPartido()` - Configuración estándar
- `crearPartidoPersonalizado()` - Con parámetros específicos

---

## 6. Adapter

**Propósito**: Unificar servicios de notificación

**Interface**: `NotificacionServiceAdapter`

**Adapters**:
- `EmailServiceAdapter` - JavaMail
- `FirebaseServiceAdapter` - Firebase FCM

---

## Integración

Los patrones trabajan juntos:

1. **Factory** crea partido
2. **Observer** se suscribe a cambios
3. **State** gestiona transiciones
4. **Strategy** valida compatibilidad
5. **Adapter** envía notificaciones
6. **MVC** orquesta todo
