package com.example.walok_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogIn;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        dbHelper = new UserDatabaseHelper(this);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });
    }

    private void validarLogin() {
        String email = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            // 🔐 Guardar el usuario en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("walok_prefs", MODE_PRIVATE);
            prefs.edit().putString("usuario", email).apply();

            Toast.makeText(this, "Bienvenido " + email, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();
        } else {
            mostrarAlerta();
        }

        cursor.close();
        db.close();
    }

    private void mostrarAlerta() {
        new AlertDialog.Builder(this)
                .setTitle("Error de autenticación")
                .setMessage("Usuario o contraseña incorrectos")
                .setCancelable(false)
                .setPositiveButton("Intentar de nuevo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etUsername.setText("");
                        etPassword.setText("");
                        etUsername.requestFocus();
                    }
                })
                .show();
    }
}
