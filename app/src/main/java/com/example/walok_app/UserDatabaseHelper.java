package com.example.walok_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "WaLokUsers.db";
    private static final int DB_VERSION = 1;

    public UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "password TEXT)");

        db.execSQL("CREATE TABLE pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mesa TEXT, " +
                "plato TEXT, " +
                "cantidad INTEGER, " +
                "precio DOUBLE,"+
                "total DOUBLE)");

        db.execSQL("CREATE TABLE pagos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "pedidoId INTEGER, " +
                "total REAL)");

        db.execSQL("CREATE TABLE cierres (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "responsable TEXT, " +
                "fecha TEXT, " +
                "boletas INTEGER, " +
                "ventas REAL)");


        // Insertar usuarios "en duro"
        db.execSQL("INSERT INTO users (email, password) VALUES ('wilber@walok.com', '12345678')");
        db.execSQL("INSERT INTO users (email, password) VALUES ('usuario@walok.com', '12345678')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No se implementa en este caso
    }
}