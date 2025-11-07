# Copilot Instructions: Uno Mas Sports Match System

## Architecture Overview

**Spring Boot 3.2.0** REST API implementing **6 design patterns** for sports match coordination. This is an academic project (Análisis y Diseño Orientado a Objetos) demonstrating pattern integration in a real-world scenario.

### Core Pattern Integration

The system's power comes from how patterns work together, not in isolation:

1. **Factory** (`PartidoFactory`, `EmparejamientoStrategyFactory`) creates domain objects
2. **State** (`EstadoPartido` interface + 6 states) manages match lifecycle transitions
3. **Strategy** (2 types: `EmparejamientoStrategy` for matching, `IStrategiaNotificacion` for notifications)
4. **Observer** (`IObservable`/`IListener`) tracks match events
5. **Adapter** (`NotificacionServiceAdapter` implementations) unifies external services (JavaMail, Firebase)
6. **MVC** separates concerns across Controller/Service/Repository/Model layers

**Critical flow:** When a user joins a match and completes the team:
- `MatcherService.unirseAPartido()` coordinates the join
- `Partido.agregarJugador()` triggers state check via **State pattern**
- State changes invoke `notificarObservadores()` (**Observer pattern**)
- Each `PartidoListener` delegates to a **Strategy** (Email/Push)
- Strategies use **Adapters** to send notifications via external services

## Key Domain Model Details

### Partido (Match) Entity
- Implements `IObservable` for event notifications
- Contains `@Transient EstadoPartido estado` - state object NOT persisted (recreated from `estadoActual` String via `@PostLoad`)
- Contains `@Transient List<IListener> observers` - reconfigured on load/player join
- `agregarJugador()` automatically transitions state when team is complete

### State Transitions
```
BUSCANDO_JUGADORES → PARTIDO_ARMADO (auto: when team fills)
PARTIDO_ARMADO → CONFIRMADO (manual: organizer confirms)
CONFIRMADO → EN_JUEGO (manual: match starts)
EN_JUEGO → FINALIZADO (manual: match ends)
Any pre-game state → CANCELADO (manual: cancellation)
```

### Value Objects
- `Ubicacion`: Embedded JPA object with `longitud`/`latitud`, includes Haversine distance calculation
- Always use factory methods to construct domain objects consistently

### Observer Reconfiguration
When players join, call `PartidoService.reconfigurarObservers()` to rebuild listener list. Each user gets observers based on notification preferences (`notificacionesEmail`, `notificacionesPush`).

## Development Workflows

### Build & Run
```bash
./mvnw clean package -DskipTests  # Compile (produces target/unomas-backend-1.0.0.jar)

# Load environment variables (if using real email/firebase)
export $(cat .env | xargs) && ./run.sh start

# Or direct JAR execution
export $(cat .env | xargs) && java -jar target/unomas-backend-1.0.0.jar
```

### Testing API
```bash
./run.sh test                     # Basic integration test (creates user + match)
./run.sh email user@test.com     # Test email notifications
./run.sh verify                  # Check configuration status
```

### Database Access
- H2 in-memory (data lost on restart): http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:unomas` | User: `sa` | Password: (empty)
- Schema auto-created from JPA entities on startup (`spring.jpa.hibernate.ddl-auto=create-drop`)

### API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Project-Specific Conventions

### Factory Usage Pattern
**Always use factories** to create domain objects. Direct constructors lead to inconsistent state initialization:
```java
// ✅ Correct
Partido partido = partidoFactory.crearPartido(tipoDeporte, organizador, ...);

// ❌ Wrong - misses state/observer setup
Partido partido = new Partido();
```

### Service Layer Boundaries
- `PartidoService`: Owns business logic for matches (create, state transitions, observer config)
- `MatcherService`: Specialized for join/leave operations (coordinates Usuario ↔ Partido bidirectional relationship)
- `UsuarioService`: User management only

When adding join/leave logic, put it in `MatcherService`, not `PartidoService`.

### DTO Mapping Convention
All DTOs mirror domain objects but flatten `Ubicacion`:
```java
// Domain
Partido.ubicacion.longitud / latitud

// DTO
PartidoDTO.longitud / latitud (flat)
```

Always extract coordinates when mapping: `dto.setLongitud(partido.getUbicacion().getLongitud())`

### Strategy Selection
Use `EmparejamientoStrategyFactory.crearEstrategia(TipoEstrategia)` to get strategies at runtime:
- `NIVEL_HABILIDAD`: Filters by `Usuario.nivelJuego` compatibility
- `CERCANIA`: Uses `Ubicacion.calcularDistancia()` for proximity
- `HISTORIAL`: Matches based on past games (placeholder implementation)

Never instantiate strategies directly in controllers/services.

### External Service Configuration

**Email (JavaMail)**: Requires environment variables. Set up credentials:
```bash
# Copy template and configure
cp .env.example .env
# Edit .env with your Gmail credentials
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password  # Gmail app password, not regular password
```

**Firebase Push**: Disabled by default (`firebase.enabled=false`). Enable by:
1. Place valid `firebase-service-account.json` in `src/main/resources/` (gitignored)
2. Set `firebase.enabled=true` in `application.properties`

**Security**: Never commit `.env` or `firebase-service-account.json` - both are gitignored. See `ENVIRONMENT.md` for setup details.

## Common Pitfalls

### @Transient State Loss
`EstadoPartido` and observers are `@Transient` - they don't persist. After loading from DB, `@PostLoad` reconstructs state from `estadoActual` String. Observers must be reconfigured manually after fetch.

### Bidirectional Relationship Management
`Usuario ↔ Partido` is `@ManyToMany`. When joining:
1. Add to `partido.jugadores` collection
2. Add to `usuario.partidosInscritos` collection
3. Reconfigure observers BEFORE triggering state change
4. Save both entities in same transaction

Missing any step breaks the relationship or notification system.

### State Transition Validation
Each `EstadoPartido` implementation validates legal transitions. Attempting invalid transitions (e.g., `finalizar()` from `BUSCANDO_JUGADORES`) throws `IllegalStateException`. Always check current state before operations.

### Lombok & JPA Integration
Use `@Builder.Default` for collections:
```java
@Builder.Default
private List<Usuario> jugadores = new ArrayList<>();
```
Without it, builder creates null collections causing NPE.

## Testing Notes

- No automated test suite yet (compiled with `-DskipTests`)
- Manual testing via `./run.sh test` or Swagger UI
- To add tests: Use `@SpringBootTest` + `@Transactional` for integration tests
- Mock external services (`EmailServiceAdapter`, `FirebaseServiceAdapter`) in unit tests

## Code References

- Pattern integration example: `PartidoService.crearPartido()` (lines ~50-100)
- State transitions: `EstadoPartido.java` + all `*State.java` implementations
- Observer setup: `PartidoService.configurarObservers()` (lines ~330-360)
- Factory pattern: `PartidoFactory.java`, `EmparejamientoStrategyFactory.java`
- Match join flow: `MatcherService.unirseAPartido()` (demonstrates coordinated pattern usage)

See `PATRONES.md` for pattern details, `IMPLEMENTACION.md` for implementation verification.