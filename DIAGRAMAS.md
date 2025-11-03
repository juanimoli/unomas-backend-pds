# DIAGRAMAS UML - UNO MAS

## 1. Diagrama de Clases Principal (Simplificado)

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         PATRÓN MVC                                        │
├──────────────────────────────────────────────────────────────────────────┤

┌─────────────────────┐         ┌─────────────────────┐
│   <<Controller>>    │         │    <<Controller>>   │
│  UsuarioController  │         │  PartidoController  │
├─────────────────────┤         ├─────────────────────┤
│ +registrar()        │         │ +crearPartido()     │
│ +obtener()          │         │ +buscarPartidos()   │
│ +actualizar()       │         │ +unirseAPartido()   │
└──────────┬──────────┘         └──────────┬──────────┘
           │                               │
           │ usa                          │ usa
           ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│   <<Service>>       │         │    <<Service>>      │
│  UsuarioService     │◄────────┤  PartidoService     │
├─────────────────────┤  usa    ├─────────────────────┤
│ +registrarUsuario() │         │ +crearPartido()     │
│ +obtenerUsuario()   │         │ +buscarPartidos()   │
└──────────┬──────────┘         └──────────┬──────────┘
           │                               │
           │ usa                          │ usa
           ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│  <<Repository>>     │         │   <<Repository>>    │
│ UsuarioRepository   │         │ PartidoRepository   │
├─────────────────────┤         ├─────────────────────┤
│ +findById()         │         │ +findById()         │
│ +findByEmail()      │         │ +findByEstado()     │
│ +save()             │         │ +save()             │
└──────────┬──────────┘         └──────────┬──────────┘
           │                               │
           │ persiste                     │ persiste
           ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│    <<Entity>>       │         │    <<Entity>>       │
│      Usuario        │         │      Partido        │
├─────────────────────┤         ├─────────────────────┤
│ -id: Long           │         │ -id: Long           │
│ -nombreUsuario      │         │ -tipoDeporte        │
│ -email              │         │ -ubicacion          │
│ -nivelJuego         │◄───┐    │ -fechaHora          │
│ -deporteFavorito    │    │    │ -estadoActual       │
└─────────────────────┘    │    │ -organizador        │
                           └────┤ -jugadores[]        │
                                │ -estado             │
                                └─────────────────────┘
```

---

## 2. Patrón STRATEGY - Emparejamiento

```
┌──────────────────────────────────────────────────────────────┐
│                    PATRÓN STRATEGY                            │
├──────────────────────────────────────────────────────────────┤

                ┌─────────────────────────────┐
                │      <<Interface>>          │
                │  EmparejamientoStrategy     │
                ├─────────────────────────────┤
                │ +esCompatible()             │
                │ +ordenarPorCompatibilidad() │
                │ +calcularCompatibilidad()   │
                └──────────────┬──────────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
┌─────────────────────┐ ┌──────────────┐ ┌─────────────────┐
│NivelHabilidadStrategy│ │CercaniaStrategy│ │HistorialStrategy│
├─────────────────────┤ ├──────────────┤ ├─────────────────┤
│ +esCompatible()     │ │+esCompatible()│ │+esCompatible()  │
│ Evalúa por nivel    │ │Evalúa por    │ │Evalúa por       │
│ de habilidad        │ │distancia     │ │historial previo │
└─────────────────────┘ └──────────────┘ └─────────────────┘
```

---

## 3. Patrón STATE - Estados del Partido

```
┌──────────────────────────────────────────────────────────────┐
│                      PATRÓN STATE                             │
├──────────────────────────────────────────────────────────────┤

                  ┌──────────────────────┐
                  │   <<Interface>>      │
                  │   EstadoPartido      │
                  ├──────────────────────┤
                  │ +equipoCompleto()    │
                  │ +confirmar()         │
                  │ +iniciar()           │
                  │ +finalizar()         │
                  │ +cancelar()          │
                  └──────────┬───────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
┌───────────────────┐  ┌──────────────┐  ┌────────────┐
│NecesitamosJugadores│  │PartidoArmado │  │Confirmado  │
│      State        │  │    State     │  │   State    │
└───────┬───────────┘  └──────┬───────┘  └─────┬──────┘
        │                     │                 │
        │ equipoCompleto()    │ confirmar()    │ iniciar()
        └──────────►──────────┴──────►─────────┴─────►
                                                       │
              ┌────────────────────────────────────────┘
              │
              ▼
     ┌────────────────┐
     │   EnJuego      │
     │    State       │
     └────────┬───────┘
              │ finalizar()
              ▼
     ┌────────────────┐
     │  Finalizado    │
     │    State       │
     └────────────────┘

     Desde cualquier estado (antes de EnJuego):
                    cancelar()
                       ↓
              ┌────────────────┐
              │   Cancelado    │
              │     State      │
              └────────────────┘
```

---

## 4. Patrón OBSERVER - Notificaciones

```
┌──────────────────────────────────────────────────────────────┐
│                    PATRÓN OBSERVER                            │
├──────────────────────────────────────────────────────────────┤

┌──────────────────────────┐
│  PartidoObservable       │ ◄──── extends ──────┐
├──────────────────────────┤                     │
│ -observers: List         │              ┌──────────────┐
│ +agregarObserver()       │              │   Partido    │
│ +eliminarObserver()      │              ├──────────────┤
│ +notificarObservadores() │              │ (hereda de   │
└────────┬─────────────────┘              │  Observable) │
         │                                └──────────────┘
         │ notifica
         ▼
┌──────────────────────────┐
│   <<Interface>>          │
│  NotificacionObserver    │
├──────────────────────────┤
│ +actualizar(partido, msg)│
└────────┬─────────────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌─────────────────┐  ┌──────────────────┐
│EmailNotification│  │PushNotification  │
│    Observer     │  │    Observer      │
├─────────────────┤  ├──────────────────┤
│ -emailAdapter   │  │ -firebaseAdapter │
│ +actualizar()   │  │ +actualizar()    │
└─────────────────┘  └──────────────────┘
```

---

## 5. Patrón ADAPTER - Servicios de Notificación

```
┌──────────────────────────────────────────────────────────────┐
│                    PATRÓN ADAPTER                             │
├──────────────────────────────────────────────────────────────┤

         ┌────────────────────────────┐
         │      <<Interface>>         │
         │NotificacionServiceAdapter  │
         ├────────────────────────────┤
         │ +enviarNotificacion()      │
         │ +isDisponible()            │
         └──────────┬─────────────────┘
                    │
          ┌─────────┴─────────┐
          │                   │
          ▼                   ▼
┌──────────────────┐  ┌──────────────────┐
│EmailServiceAdapter│  │FirebaseService   │
│                  │  │    Adapter       │
├──────────────────┤  ├──────────────────┤
│ -mailSender      │  │ -firebaseEnabled │
│ +enviar()        │  │ +enviar()        │
└────────┬─────────┘  └────────┬─────────┘
         │ adapta              │ adapta
         ▼                     ▼
┌──────────────────┐  ┌──────────────────┐
│  JavaMailSender  │  │Firebase Messaging│
│  (Spring Mail)   │  │   (Firebase SDK) │
└──────────────────┘  └──────────────────┘
```

---

## 6. Patrón FACTORY - Creación de Partidos

```
┌──────────────────────────────────────────────────────────────┐
│                    PATRÓN FACTORY                             │
├──────────────────────────────────────────────────────────────┤

┌─────────────────────────────────┐
│       PartidoFactory            │
├─────────────────────────────────┤
│ +crearPartido()                 │
│ +crearPartidoPersonalizado()    │
│ +crearPartidoRapido()           │
└────────────┬────────────────────┘
             │ crea
             ▼
    ┌─────────────────┐
    │     Partido     │
    ├─────────────────┤
    │ con estado      │
    │ inicial y       │
    │ configuración   │
    └─────────────────┘
```

---

## 7. Diagrama de Secuencia - Crear y Unirse a Partido

```
Cliente  Controller  Service   Factory   Repository  Partido  Observers
  │         │          │         │           │         │         │
  │─crear──>│          │         │           │         │         │
  │         │──crear──>│         │           │         │         │
  │         │          │─crear──>│           │         │         │
  │         │          │         │─new()───>│         │         │
  │         │          │         │<─Partido─┤         │         │
  │         │          │<─partido┤           │         │         │
  │         │          │─save()────────────>│         │         │
  │         │          │<─partidoGuardado───┤         │         │
  │         │          │─configurarObservers────────>│         │
  │         │          │                              │─agregar>│
  │         │<─DTO─────┤                              │         │
  │<─201────┤          │                              │         │
  │         │          │                              │         │
  │─unirse─>│          │                              │         │
  │         │─unirse──>│                              │         │
  │         │          │─obtenerPartido────────────>│         │
  │         │          │<─partido────────────────────┤         │
  │         │          │─verificarCompatibilidad     │         │
  │         │          │─agregar(usuario)───────────>│         │
  │         │          │                              │─notif──>│
  │         │          │                              │         │─enviar
  │         │<─DTO─────┤                              │         │
  │<─200────┤          │                              │         │
```

---

## 8. Diagrama de Transición de Estados

```
                    ┌─────────────────────┐
                    │  NECESITAMOS        │
                    │    JUGADORES        │
                    │   (Estado Inicial)  │
                    └──────────┬──────────┘
                               │
                    equipoCompleto()
                               │
                               ▼
                    ┌─────────────────────┐
                    │  PARTIDO ARMADO     │
                    │ (Equipo completo)   │
                    └──────────┬──────────┘
                               │
                        confirmar()
                               │
                               ▼
                    ┌─────────────────────┐
                    │   CONFIRMADO        │
                    │ (Todos aceptaron)   │
                    └──────────┬──────────┘
                               │
                    iniciar() / auto-inicio
                               │
                               ▼
                    ┌─────────────────────┐
                    │    EN JUEGO         │
                    │ (Partido en curso)  │
                    └──────────┬──────────┘
                               │
                        finalizar()
                               │
                               ▼
                    ┌─────────────────────┐
                    │    FINALIZADO       │
                    │ (Partido terminado) │
                    └─────────────────────┘

    Desde NECESITAMOS_JUGADORES, PARTIDO_ARMADO o CONFIRMADO:
                               │
                        cancelar()
                               │
                               ▼
                    ┌─────────────────────┐
                    │     CANCELADO       │
                    │  (Partido cancelado)│
                    └─────────────────────┘
```

---

## 9. Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────────┐
│                    ARQUITECTURA DEL SISTEMA                   │
└──────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Capa de Presentación                      │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐       │
│  │   Swagger   │  │   REST API  │  │  Controllers │       │
│  │     UI      │  │  Endpoints  │  │              │       │
│  └─────────────┘  └─────────────┘  └──────────────┘       │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                    Capa de Negocio                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Services   │  │   Strategy   │  │   Factory    │     │
│  │              │  │   Observer   │  │    State     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────────┐
│                  Capa de Persistencia                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Repositories │  │   Entities   │  │   H2 / DB    │     │
│  │              │  │              │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└──────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────┐
│                  Servicios Externos                           │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │   JavaMail   │  │   Firebase   │                         │
│  │   (Email)    │  │    (Push)    │                         │
│  └──────────────┘  └──────────────┘                         │
└──────────────────────────────────────────────────────────────┘
```

---

## Notas sobre los Diagramas

Los diagramas presentados son representaciones textuales de los diagramas UML.
Para la presentación final, se recomienda crear versiones gráficas usando
herramientas como:

- PlantUML
- Lucidchart
- Draw.io
- Visual Paradigm
- StarUML

Estos diagramas muestran claramente:
1. La estructura de clases y relaciones
2. La implementación de cada patrón de diseño
3. El flujo de ejecución de operaciones clave
4. La arquitectura general del sistema
