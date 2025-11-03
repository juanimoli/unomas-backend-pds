# Documentación de Patrones de Diseño Implementados

## 1. Patrón MVC (Model-View-Controller)

### Descripción
El patrón MVC separa la aplicación en tres componentes principales:

### Implementación en Uno Mas
- **Model**: Clases en `com.unomas.model` (Usuario, Partido, TipoDeporte)
- **Controller**: Clases en `com.unomas.controller` (UsuarioController, PartidoController)
- **Service**: Capa de lógica de negocio en `com.unomas.service` (UsuarioService, PartidoService)

### Ubicación
- `src/main/java/com/unomas/model/`
- `src/main/java/com/unomas/controller/`
- `src/main/java/com/unomas/service/`

---

## 2. Patrón Strategy

### Descripción
Define una familia de algoritmos, encapsula cada uno y los hace intercambiables.

### Implementación en Uno Mas
Se utiliza para implementar diferentes estrategias de emparejamiento de jugadores:

- **EmparejamientoStrategy** (Interface): Define la operación común
- **NivelHabilidadStrategy**: Empareja jugadores por nivel de habilidad
- **CercaniaStrategy**: Empareja jugadores por cercanía geográfica  
- **HistorialStrategy**: Empareja jugadores por historial de partidos previos

### Uso
```java
EmparejamientoStrategy strategy = new NivelHabilidadStrategy();
boolean esCompatible = strategy.esCompatible(usuario, partido);
```

### Ubicación
`src/main/java/com/unomas/strategy/`

---

## 3. Patrón State

### Descripción
Permite que un objeto altere su comportamiento cuando su estado interno cambia.

### Implementación en Uno Mas
Gestiona los diferentes estados de un partido:

- **EstadoPartido** (Interface): Define operaciones comunes
- **NecesitamosJugadoresState**: Estado inicial
- **PartidoArmadoState**: Cuando se completa el equipo
- **ConfirmadoState**: Todos los jugadores confirmaron
- **EnJuegoState**: Partido en curso
- **FinalizadoState**: Partido terminado
- **CanceladoState**: Partido cancelado

### Transiciones
```
NECESITAMOS_JUGADORES → PARTIDO_ARMADO → CONFIRMADO → EN_JUEGO → FINALIZADO
                     ↓         ↓            ↓
                          CANCELADO
```

### Uso
```java
partido.getEstado().confirmar(partido);
partido.getEstado().iniciar(partido);
```

### Ubicación
`src/main/java/com/unomas/state/`

---

## 4. Patrón Observer

### Descripción
Define una dependencia uno-a-muchos entre objetos para que cuando uno cambie de estado, todos sus dependientes sean notificados.

### Implementación en Uno Mas
Sistema de notificaciones para eventos del partido:

- **PartidoObservable**: Subject que mantiene lista de observers
- **NotificacionObserver** (Interface): Interface para observers
- **EmailNotificationObserver**: Envía notificaciones por email
- **PushNotificationObserver**: Envía notificaciones push

### Uso
```java
partido.agregarObserver(new EmailNotificationObserver(emailAdapter));
partido.notificarObservadores("Partido confirmado");
```

### Ubicación
`src/main/java/com/unomas/observer/`

---

## 5. Patrón Factory

### Descripción
Define una interfaz para crear objetos, pero deja que las subclases decidan qué clase instanciar.

### Implementación en Uno Mas
- **PartidoFactory**: Crea instancias de partidos con diferentes configuraciones

### Métodos
- `crearPartido()`: Partido con configuración predeterminada
- `crearPartidoPersonalizado()`: Partido con configuración personalizada
- `crearPartidoRapido()`: Partido con configuración simplificada

### Uso
```java
Partido partido = partidoFactory.crearPartido(
    TipoDeporte.FUTBOL,
    organizador,
    fechaHora,
    ubicacion,
    direccion
);
```

### Ubicación
`src/main/java/com/unomas/factory/`

---

## 6. Patrón Adapter

### Descripción
Convierte la interfaz de una clase en otra interfaz que el cliente espera.

### Implementación en Uno Mas
Adapta diferentes servicios de notificación a una interfaz común:

- **NotificacionServiceAdapter** (Interface): Interface común
- **EmailServiceAdapter**: Adapta JavaMailSender
- **FirebaseServiceAdapter**: Adapta Firebase Cloud Messaging

### Uso
```java
NotificacionServiceAdapter emailAdapter = new EmailServiceAdapter(mailSender);
emailAdapter.enviarNotificacion(destinatario, titulo, mensaje);
```

### Ubicación
`src/main/java/com/unomas/adapter/`

---

## Integración de Patrones

El sistema integra todos los patrones de forma cohesiva:

1. **MVC** estructura toda la aplicación
2. **Factory** crea los partidos
3. **State** gestiona las transiciones de estado
4. **Observer** notifica los cambios a través de **Adapters**
5. **Strategy** determina la compatibilidad de jugadores

### Ejemplo de Flujo Completo:

```java
// 1. Factory crea el partido
Partido partido = partidoFactory.crearPartido(...);

// 2. Observer pattern: configurar notificaciones
partido.agregarObserver(new EmailNotificationObserver(emailAdapter));

// 3. Strategy pattern: verificar compatibilidad
if (nivelHabilidadStrategy.esCompatible(usuario, partido)) {
    // 4. State pattern: agregar jugador puede cambiar estado
    partido.agregarJugador(usuario);
    
    // 5. Observer notifica automáticamente el cambio
    // 6. Adapter envía las notificaciones
}
```

Este diseño hace el sistema:
- **Mantenible**: Cada patrón tiene responsabilidades claras
- **Extensible**: Fácil agregar nuevas estrategias, estados o notificaciones
- **Testeable**: Componentes desacoplados
- **Profesional**: Sigue principios SOLID
