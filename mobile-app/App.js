import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, Platform, Alert } from 'react-native';
import { useEffect, useRef, useState } from 'react';
import * as Device from 'expo-device';
import * as Notifications from 'expo-notifications';
import Constants from 'expo-constants';

// Configurar cómo se manejan las notificaciones cuando la app está en primer plano
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
  }),
});

export default function App() {
  const [expoPushToken, setExpoPushToken] = useState('');
  const [notification, setNotification] = useState(false);
  const notificationListener = useRef();
  const responseListener = useRef();

  useEffect(() => {
    registerForPushNotificationsAsync().then(token => {
      setExpoPushToken(token);
    });

    notificationListener.current = Notifications.addNotificationReceivedListener(notification => {
      setNotification(notification);
    });

    responseListener.current = Notifications.addNotificationResponseReceivedListener(response => {
      // Handle notification interaction
    });

    return () => {
      Notifications.removeNotificationSubscription(notificationListener.current);
      Notifications.removeNotificationSubscription(responseListener.current);
    };
  }, []);

  return (
    <View style={styles.container}>
      <View style={styles.logoContainer}>
        <Text style={styles.logo}>⚽🏀🎾</Text>
      </View>
      
      <Text style={styles.title}>Uno Más</Text>
      <Text style={styles.subtitle}>Sistema de Encuentros Deportivos</Text>
      
      <View style={styles.infoContainer}>
        <Text style={styles.infoText}>
          ¡Listo para recibir notificaciones!
        </Text>
        
        {expoPushToken ? (
          expoPushToken.startsWith('ERROR:') ? (
            <View style={styles.errorContainer}>
              <Text style={styles.errorTitle}>⚠️ Limitación de Expo Go</Text>
              <Text style={styles.errorText}>
                Las notificaciones push no están soportadas en Expo Go con SDK 53+.
              </Text>
              <Text style={styles.errorHint}>
                Para probar notificaciones push, necesitas crear un Development Build.
                Por ahora, la app funciona en modo demostración.
              </Text>
            </View>
          ) : (
            <View style={styles.tokenContainer}>
              <Text style={styles.tokenLabel}>Tu token de notificaciones:</Text>
              <Text style={styles.tokenText} numberOfLines={3}>
                {expoPushToken}
              </Text>
              <Text style={styles.tokenHint}>
                Usa este token en el backend para enviar notificaciones push
              </Text>
            </View>
          )
        ) : (
          <Text style={styles.loadingText}>Obteniendo token...</Text>
        )}

        {notification && (
          <View style={styles.notificationBox}>
            <Text style={styles.notificationTitle}>
              📬 Última notificación recibida:
            </Text>
            <Text style={styles.notificationText}>
              {notification.request.content.title}
            </Text>
            <Text style={styles.notificationBody}>
              {notification.request.content.body}
            </Text>
          </View>
        )}
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerText}>
          Encuentra jugadores para tus partidos
        </Text>
        <Text style={styles.versionText}>
          v1.0.0 - Modo Demo
        </Text>
      </View>

      <StatusBar style="auto" />
    </View>
  );
}

// Función para registrar el dispositivo y obtener el token de push
async function registerForPushNotificationsAsync() {
  let token;

  if (Platform.OS === 'android') {
    await Notifications.setNotificationChannelAsync('default', {
      name: 'default',
      importance: Notifications.AndroidImportance.MAX,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: '#FF231F7C',
    });
  }

  if (Device.isDevice) {
    const { status: existingStatus } = await Notifications.getPermissionsAsync();
    let finalStatus = existingStatus;
    
    if (existingStatus !== 'granted') {
      const { status } = await Notifications.requestPermissionsAsync();
      finalStatus = status;
    }
    
    if (finalStatus !== 'granted') {
      alert('Error: No se obtuvieron permisos para notificaciones push');
      return;
    }
    
    try {
      const projectId = Constants.expoConfig?.extra?.eas?.projectId;
      const pushTokenData = await Notifications.getExpoPushTokenAsync({
        projectId: projectId,
      });
      token = pushTokenData.data;
    } catch (error) {
      return 'ERROR: ' + error.message;
    }
  } else {
    alert('Debes usar un dispositivo físico para recibir notificaciones push');
  }

  return token;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 20,
  },
  logoContainer: {
    marginBottom: 20,
  },
  logo: {
    fontSize: 60,
    textAlign: 'center',
  },
  title: {
    fontSize: 42,
    fontWeight: 'bold',
    color: '#2c3e50',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: '#7f8c8d',
    marginBottom: 30,
    textAlign: 'center',
  },
  infoContainer: {
    width: '100%',
    backgroundColor: 'white',
    borderRadius: 12,
    padding: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
    marginBottom: 20,
  },
  infoText: {
    fontSize: 16,
    color: '#27ae60',
    fontWeight: '600',
    marginBottom: 15,
    textAlign: 'center',
  },
  tokenContainer: {
    backgroundColor: '#ecf0f1',
    padding: 15,
    borderRadius: 8,
    marginTop: 10,
  },
  tokenLabel: {
    fontSize: 12,
    color: '#7f8c8d',
    fontWeight: '600',
    marginBottom: 8,
  },
  tokenText: {
    fontSize: 11,
    color: '#2c3e50',
    fontFamily: Platform.OS === 'ios' ? 'Courier' : 'monospace',
    marginBottom: 8,
  },
  tokenHint: {
    fontSize: 11,
    color: '#95a5a6',
    fontStyle: 'italic',
  },
  loadingText: {
    fontSize: 14,
    color: '#95a5a6',
    textAlign: 'center',
    marginTop: 10,
  },
  errorContainer: {
    backgroundColor: '#fff3cd',
    padding: 15,
    borderRadius: 8,
    borderLeftWidth: 4,
    borderLeftColor: '#ffc107',
    marginTop: 10,
  },
  errorTitle: {
    fontSize: 14,
    color: '#856404',
    fontWeight: '600',
    marginBottom: 8,
  },
  errorText: {
    fontSize: 13,
    color: '#856404',
    marginBottom: 8,
  },
  errorHint: {
    fontSize: 12,
    color: '#856404',
    fontStyle: 'italic',
  },
  notificationBox: {
    backgroundColor: '#3498db',
    padding: 15,
    borderRadius: 8,
    marginTop: 15,
  },
  notificationTitle: {
    fontSize: 14,
    color: 'white',
    fontWeight: '600',
    marginBottom: 8,
  },
  notificationText: {
    fontSize: 16,
    color: 'white',
    fontWeight: 'bold',
    marginBottom: 4,
  },
  notificationBody: {
    fontSize: 14,
    color: 'white',
  },
  footer: {
    marginTop: 'auto',
    alignItems: 'center',
  },
  footerText: {
    fontSize: 14,
    color: '#7f8c8d',
    marginBottom: 5,
  },
  versionText: {
    fontSize: 12,
    color: '#bdc3c7',
  },
});
