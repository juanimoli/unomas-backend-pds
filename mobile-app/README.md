# Uno Más - Mobile App

React Native app con Expo SDK 54 para notificaciones push del sistema de encuentros deportivos.

## Setup

```bash
npm install
npx expo run:ios
```

## Configuración

- **Project ID**: `069d1177-647d-4544-b371-4e1361940b78`
- **Bundle ID iOS**: `com.unomas.mobile`
- **Package Android**: `com.unomas.mobile`

## Integración Backend

Token de push se registra automáticamente:
```
PUT /api/usuarios/{id}/push-token
```

## Build Producción

```bash
# iOS
eas build --platform ios --profile production

# Android
eas build --platform android --profile production
```
