package com.unomas.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    
    private TextView tvToken;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvToken = findViewById(R.id.tvToken);
        tvStatus = findViewById(R.id.tvStatus);

        // Solicitar permisos de notificación en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                obtenerToken();
            }
        } else {
            obtenerToken();
        }
    }

    private void obtenerToken() {
        tvStatus.setText("Obteniendo token...");
        
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            tvStatus.setText("❌ Error obteniendo token");
                            tvToken.setText("Error: " + task.getException().getMessage());
                            return;
                        }

                        // Token obtenido
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                        
                        tvStatus.setText("✅ Token obtenido exitosamente");
                        tvToken.setText(token);
                        
                        Toast.makeText(MainActivity.this, "Token FCM obtenido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerToken();
            } else {
                tvStatus.setText("⚠️ Permiso de notificaciones denegado");
                Toast.makeText(this, "Permisos de notificación necesarios", Toast.LENGTH_LONG).show();
            }
        }
    }
}
