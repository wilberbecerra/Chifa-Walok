package com.example.walok_app;
public class Plato {
    public String nombre;
    public double precio;

    public Plato(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre; // Solo se mostrará el nombre en el Spinner
    }
}

