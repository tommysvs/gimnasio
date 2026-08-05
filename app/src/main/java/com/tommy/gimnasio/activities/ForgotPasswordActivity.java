package com.tommy.gimnasio.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.tommy.gimnasio.R;
import com.tommy.gimnasio.database.DatabaseHelper;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etResetEmail;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DatabaseHelper(this);
        etResetEmail = findViewById(R.id.etResetEmail);
        Button btnVerify = findViewById(R.id.btnVerify);
        android.widget.TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnVerify.setOnClickListener(v -> {
            if (etResetEmail.getText() == null) return;
            
            String email = etResetEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.verificarCorreo(email)) {
                showNewPasswordDialog(email);
            } else {
                Toast.makeText(this, "El correo no está registrado o el usuario está inactivo", Toast.LENGTH_SHORT).show();
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void showNewPasswordDialog(String email) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reset_password, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirmReset);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelReset);

        btnConfirm.setOnClickListener(v -> {
            if (etNewPassword.getText() == null) return;
            String newPass = etNewPassword.getText().toString().trim();
            
            if (newPass.isEmpty() || newPass.length() < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.actualizarPassword(email, newPass)) {
                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
