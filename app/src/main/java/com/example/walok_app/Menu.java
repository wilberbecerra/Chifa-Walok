package com.example.walok_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {

    Button btnPedido, btnCaja, btnCierreCaja, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Asignar botones
        btnPedido = findViewById(R.id.btnPedido);
        btnCaja = findViewById(R.id.btnCaja);
        btnCierreCaja = findViewById(R.id.btnCierreCaja);

        // Ir a Pedido
        btnPedido.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Pedido.class);
            startActivity(intent);
        });

        // Ir a Caja
        btnCaja.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Caja.class);
            startActivity(intent);
        });

        // Ir a Cierre de Caja
        btnCierreCaja.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, CierreCaja.class);
            startActivity(intent);
        });

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiar sesión guardada
            getSharedPreferences("walok_prefs", MODE_PRIVATE)
                    .edit()
                    .remove("usuario")
                    .apply();

            // Volver al login y limpiar pila de actividades
            Intent intent = new Intent(Menu.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }
}
