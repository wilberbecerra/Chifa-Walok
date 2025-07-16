package com.example.walok_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CierreCaja extends AppCompatActivity {

    EditText etResponsable, etFecha, etBoletas, etVentas;
    Button btnCerrarCaja;
    TextView btnCerrar;
    UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_caja);

        etResponsable = findViewById(R.id.etResponsable);
        etFecha = findViewById(R.id.etFecha);
        etBoletas = findViewById(R.id.etBoletas);
        etVentas = findViewById(R.id.etVentas);
        btnCerrarCaja = findViewById(R.id.btnCerrarCaja);
        btnCerrar = findViewById(R.id.btnCerrar);

        dbHelper = new UserDatabaseHelper(this);

        btnCerrar.setOnClickListener(v -> finish());

        cargarDatosAutomaticos();

        btnCerrarCaja.setOnClickListener(v -> {
            String responsable = etResponsable.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String boletas = etBoletas.getText().toString().trim();
            String ventas = etVentas.getText().toString().trim();

            if (responsable.isEmpty() || fecha.isEmpty() || boletas.isEmpty() || ventas.isEmpty()) {
                mostrarAlerta("Faltan datos", "Todos los campos son obligatorios.");
                return;
            }

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO cierres (responsable, fecha, boletas, ventas) VALUES (?, ?, ?, ?)",
                        new Object[]{responsable, fecha, Integer.parseInt(boletas), Double.parseDouble(ventas)});
                db.close();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo guardar el cierre.");
                return;
            }

            // Confirmación final
            new AlertDialog.Builder(CierreCaja.this)
                    .setTitle("¿Confirmar cierre de caja?")
                    .setMessage("¿Estás seguro de cerrar la caja? Y volver al login.")
                    .setPositiveButton("Sí, cerrar", (dialog, which) -> {
                        // Borrar sesión
                        SharedPreferences prefs = getSharedPreferences("walok_prefs", MODE_PRIVATE);
                        prefs.edit().remove("usuario").apply();

                        // Borrar base de datos
                        deleteDatabase("walok.db");

                        // Mensaje final
                        Toast.makeText(CierreCaja.this, "Caja cerrada correctamente", Toast.LENGTH_SHORT).show();

                        // Volver al login
                        Intent intent = new Intent(CierreCaja.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void cargarDatosAutomaticos() {
        // Fecha actual
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etFecha.setText(fechaHoy);

        // Usuario logueado
        SharedPreferences prefs = getSharedPreferences("walok_prefs", MODE_PRIVATE);
        String usuario = prefs.getString("usuario", "");
        etResponsable.setText(usuario);

        // Consultar total de pedidos y suma de totales
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*), SUM(total) FROM pedidos", null);
            if (cursor.moveToFirst()) {
                etBoletas.setText(String.valueOf(cursor.getInt(0)));
                etVentas.setText(String.format(Locale.getDefault(), "%.2f", cursor.getDouble(1)));
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar los datos de pedidos.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }
}
