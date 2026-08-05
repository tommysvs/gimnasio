package com.tommy.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUser, etPassword;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> handleLogin());

        tvForgotPassword.setOnClickListener(v -> 
            Toast.makeText(this, "Funcionalidad de recuperación en desarrollo", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        if (etUser.getText() == null || etPassword.getText() == null) return;

        String username = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.validarUsuario(username, password);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String rol = cursor.getString(cursor.getColumnIndexOrThrow("rol_nombre"));
                int idRol = cursor.getInt(cursor.getColumnIndexOrThrow("id_rol"));

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USER_ID", idUsuario);
                intent.putExtra("USER_NAME", nombre);
                intent.putExtra("USER_ROLE", rol);
                intent.putExtra("ROLE_ID", idRol);
                
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Error en los datos del usuario", Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Credenciales incorrectas o usuario inactivo", Toast.LENGTH_SHORT).show();
        }
    }
}
