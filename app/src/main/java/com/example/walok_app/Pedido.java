package com.example.walok_app;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Pedido extends AppCompatActivity {

    Spinner spMesa, spPlato;
    EditText etCantidad;
    Button btnGenerar;
    TextView btnCerrar;
    UserDatabaseHelper dbHelper;

    List<Plato> listaPlatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        deleteDatabase("WaLokUsers.db");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        spMesa = findViewById(R.id.spMesa);
        spPlato = findViewById(R.id.spPlato);
        etCantidad = findViewById(R.id.etCantidad);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnCerrar = findViewById(R.id.btnCerrar);

        dbHelper = new UserDatabaseHelper(this);

        // Combo de mesas
        String[] mesas = {"Seleccionar", "1", "2", "3", "4", "5"};
        ArrayAdapter<String> mesaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mesas);
        mesaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMesa.setAdapter(mesaAdapter);

        // Lista de platos con precios
        listaPlatos = new ArrayList<>();
        listaPlatos.add(new Plato("Seleccionar", 0));
        listaPlatos.add(new Plato("Arroz Chaufa", 18.0));
        listaPlatos.add(new Plato("Pollo con Verduras", 20.0));
        listaPlatos.add(new Plato("Tallarín Saltado", 16.0));
        listaPlatos.add(new Plato("Kam Lu Wantán", 22.0));

        ArrayAdapter<Plato> platoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaPlatos);
        platoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlato.setAdapter(platoAdapter);

        btnCerrar.setOnClickListener(v -> finish());

        btnGenerar.setOnClickListener(v -> {
            String mesa = spMesa.getSelectedItem().toString();
            Plato platoSeleccionado = (Plato) spPlato.getSelectedItem();
            String cantidadStr = etCantidad.getText().toString().trim();

            if (mesa.equals("Seleccionar") || platoSeleccionado.nombre.equals("Seleccionar") || cantidadStr.isEmpty()) {
                mostrarAlerta("Datos incompletos", "Todos los campos son obligatorios.");
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            double precio = platoSeleccionado.precio;
            double total = precio * cantidad;
            String plato = platoSeleccionado.nombre;

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO pedidos (mesa, plato, cantidad, precio, total) VALUES (?, ?, ?, ?, ?)",
                        new Object[]{mesa, plato, cantidad, precio, total});
                db.close();

                mostrarAlerta("Éxito", "Pedido registrado correctamente.\nTotal: S/ " + total);
                limpiarFormulario();

            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo registrar el pedido.");
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void limpiarFormulario() {
        spMesa.setSelection(0);
        spPlato.setSelection(0);
        etCantidad.setText("");
    }
}
