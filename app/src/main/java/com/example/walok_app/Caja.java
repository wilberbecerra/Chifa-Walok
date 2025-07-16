package com.example.walok_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Caja extends AppCompatActivity {

    EditText etPedidoId, etMesa, etPlato, etCantidad, etTotal;
    Button btnBuscar, btnPagar, btnCierreCaja;
    TextView btnCerrar;
    UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caja);

        etPedidoId = findViewById(R.id.etPedidoId);
        etMesa = findViewById(R.id.etMesa);
        etPlato = findViewById(R.id.etPlato);
        etCantidad = findViewById(R.id.etCantidad);
        etTotal = findViewById(R.id.etTotal);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnPagar = findViewById(R.id.btnPagar);
        btnCierreCaja = findViewById(R.id.btnCierreCaja);
        btnCerrar = findViewById(R.id.btnCerrar);

        dbHelper = new UserDatabaseHelper(this);

        btnCerrar.setOnClickListener(v -> finish());

        btnBuscar.setOnClickListener(v -> {
            String id = etPedidoId.getText().toString().trim();
            if (id.isEmpty()) {
                alerta("Campo obligatorio", "Ingresa el número de pedido.");
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT mesa, plato, cantidad, total FROM pedidos WHERE id = ?", new String[]{id});
            if (cursor.moveToFirst()) {
                etMesa.setText(cursor.getString(0));
                etPlato.setText(cursor.getString(1));
                etCantidad.setText(cursor.getString(2));
                etTotal.setText(cursor.getString(3));

                // Verificar si ya fue pagado
                Cursor cursorPago = db.rawQuery("SELECT 1 FROM pagos WHERE pedidoId = ?", new String[]{id});
                if (cursorPago.moveToFirst()) {
                    alerta("Advertencia", "Este pedido ya fue pagado.");
                    btnPagar.setEnabled(false);
                } else {
                    btnPagar.setEnabled(true);
                }
                cursorPago.close();

            } else {
                alerta("No encontrado", "El pedido no existe.");
                btnPagar.setEnabled(false);
            }
            cursor.close();
            db.close();
        });

        btnPagar.setOnClickListener(v -> {
            String pedidoId = etPedidoId.getText().toString().trim();
            String total = etTotal.getText().toString().trim();

            if (pedidoId.isEmpty() || etMesa.getText().toString().isEmpty() || total.isEmpty()) {
                alerta("Datos incompletos", "Todos los campos son obligatorios.");
                return;
            }

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO pagos (pedidoId, total) VALUES (?, ?)", new Object[]{pedidoId, total});
                db.close();
                alerta("Éxito", "Pago registrado correctamente.");
                limpiar();
            } catch (Exception e) {
                alerta("Error", "No se pudo registrar el pago.");
            }
        });

        btnCierreCaja.setOnClickListener(v -> {
            Intent intent = new Intent(Caja.this, CierreCaja.class);
            startActivity(intent);
        });
    }

    private void alerta(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    private void limpiar() {
        etPedidoId.setText("");
        etMesa.setText("");
        etPlato.setText("");
        etCantidad.setText("");
        etTotal.setText("");
        btnPagar.setEnabled(true);
    }
}
