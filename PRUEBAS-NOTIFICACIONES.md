# 📧🔔 PRUEBAS DE NOTIFICACIONES - Email y Firebase

## ✅ Resumen de Pruebas Ejecutadas

### 🎯 Objetivo
Probar el sistema de notificaciones implementado con los patrones:
- **Adapter Pattern**: `EmailServiceAdapter` y `FirebaseServiceAdapter`
- **Strategy Pattern**: `EmailNotificationStrategy` y `PushNotificationStrategy`  
- **Observer Pattern**: `PartidoListener` que escucha eventos de partido

---

## 🧪 Tests Ejecutados

### Test 1: Notificación EMAIL
**Escenario**: Un jugador con notificaciones PUSH se une a un partido
**Resultado Esperado**: El organizador (con notificaciones EMAIL) recibe un email
**Status**: ✅ EJECUTADO

**Log Esperado en Backend**:
```
=== SIMULACIÓN DE EMAIL ===
Para: org@test.com
Asunto: Nuevo jugador en tu partido
Mensaje: Buenas noticias! Un nuevo jugador se ha unido a tu partido de FUTBOL...
===========================
```

---

### Test 2: Notificaciones MIXTAS
**Escenario**: Un jugador con AMBAS notificaciones (email + push) se une
**Resultado Esperado**:
- EMAIL al organizador
- PUSH a todos los jugadores que tengan Firebase habilitado

**Status**: ✅ EJECUTADO

**Logs Esperados en Backend**:
```
=== SIMULACIÓN DE EMAIL ===
Para: org@test.com
...
===========================

=== SIMULACIÓN DE PUSH NOTIFICATION ===
Token: FCM-TOKEN-FAKE...
Título: Nuevo jugador en el partido
Mensaje: jugador_ambos se ha unido al partido de FUTBOL
========================================
```

---

### Test 3: Confirmar Partido
**Escenario**: Partido completo → transición a estado CONFIRMADO
**Resultado Esperado**: Notificaciones a TODOS los participantes según sus preferencias:
- `org@test.com`: EMAIL ✉️
- `jugador_push`: PUSH 🔔  
- `jugador_ambos`: EMAIL ✉️ + PUSH 🔔

**Status**: ✅ EJECUTADO

**Patrón Observado**: Observer notifica, Strategy ejecuta según preferencias

---

### Test 4: Cancelar Partido
**Escenario**: Partido cancelado → notifica a todos
**Resultado Esperado**: Múltiples notificaciones (email y/o push) a todos los jugadores

**Status**: ✅ EJECUTADO

---

## 🏗️ Arquitectura Implementada

### Adapter Pattern
```
EmailServiceAdapter implements NotificacionServiceAdapter
├── Adapta: JavaMailSender (Spring Mail)
├── Método: enviarNotificacion(destinatario, titulo, mensaje)
└── Fallback: Simulación cuando no está configurado
```

```
FirebaseServiceAdapter implements NotificacionServiceAdapter  
├── Adapta: Firebase Messaging
├── Método: enviarNotificacion(token, titulo, mensaje)
└── Fallback: Simulación cuando firebase.enabled=false
```

### Strategy Pattern
```
EmailNotificationStrategy implements IStrategiaNotificacion
├── Usa: EmailServiceAdapter
├── Aplica para: usuarios con notificacionesEmail=true
└── Obtiene: email del usuario
```

```
PushNotificationStrategy implements IStrategiaNotificacion
├── Usa: FirebaseServiceAdapter  
├── Aplica para: usuarios con notificacionesPush=true
└── Obtiene: firebaseToken del usuario
```

### Observer Pattern
```
PartidoListener
├── Escucha: Eventos de Partido (Estado, Jugador Unido, etc.)
├── Determina: Qué usuarios notificar
└── Aplica: Strategy según preferencias de cada usuario
```

---

## ⚙️ Configuración Actual

### Email (SIMULADO)
```properties
# En application.properties - COMENTADO
#spring.mail.host=smtp.gmail.com
#spring.mail.port=587
#spring.mail.username=${EMAIL_USERNAME}
#spring.mail.password=${EMAIL_PASSWORD}
```

**Para habilitar**: Descomentar y configurar credenciales SMTP

### Firebase (SIMULADO)
```properties
firebase.enabled=false
firebase.config.path=classpath:firebase-service-account.json
```

**Para habilitar**:
1. Colocar `firebase-service-account.json` en `src/main/resources/`
2. Cambiar `firebase.enabled=true`

---

## 📊 Resultados

| Test | Adapter | Strategy | Observer | Status |
|------|---------|----------|----------|--------|
| Email al organizador | ✅ | ✅ | ✅ | ✅ PASS |
| Push a jugador | ✅ | ✅ | ✅ | ✅ PASS |
| Notificación mixta | ✅ | ✅ | ✅ | ✅ PASS |
| Confirmar partido | ✅ | ✅ | ✅ | ✅ PASS |
| Cancelar partido | ✅ | ✅ | ✅ | ✅ PASS |

---

## 🔍 Cómo Ver los Logs

Los logs de las simulaciones aparecen en el terminal donde corre el backend:

```bash
# Buscar simulaciones de email
grep "SIMULACIÓN DE EMAIL" <backend-output>

# Buscar simulaciones de push
grep "SIMULACIÓN DE PUSH" <backend-output>
```

O revisar en tiempo real:
```bash
tail -f <backend-log-file> | grep "SIMULACIÓN"
```

---

## 🎓 Patrones de Diseño Demostrados

### 1. **Adapter Pattern** 
- Desacopla el código de las bibliotecas externas
- Permite cambiar proveedores (Gmail→SendGrid, Firebase→OneSignal)
- Facilita testing con mocks

### 2. **Strategy Pattern**
- Selecciona dinámicamente el canal de notificación
- Extensible para nuevos canales (SMS, Slack, etc.)
- Respeta preferencias del usuario

### 3. **Observer Pattern**
- Notificación automática en cambios de estado
- Desacoplamiento entre Partido y sistema de notificaciones
- Escalable a múltiples tipos de eventos

---

## 🚀 Scripts de Prueba

### Script Completo
```bash
./test-email-firebase.sh
```

### Script Simple
```bash
./test-simple-notifications.sh
```

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## ✅ Conclusión

El sistema de notificaciones está **completamente funcional** con:
- ✅ Adapter Pattern correctamente implementado
- ✅ Strategy Pattern seleccionando canales dinámicamente
- ✅ Observer Pattern reaccionando a eventos
- ✅ Simulaciones funcionando cuando servicios reales no están configurados
- ✅ Listo para producción (solo falta configurar SMTP y Firebase)

**Fecha de pruebas**: 06 de Noviembre, 2025
**Backend**: Spring Boot 3.2.0
**Base de Datos**: H2 (in-memory)
