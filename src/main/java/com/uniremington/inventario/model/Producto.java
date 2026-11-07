package com.uniremington.inventario.model;

public class Producto {

    private final String id;
    private final String nombre;
    private final double precio;
    private int stock;

    public Producto(String id, String nombre, double precio, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser positiva");
        }
        if (cantidad > stock) {
            throw new IllegalStateException("Stock insuficiente para la operación");
        }
        this.stock -= cantidad;
    }

    public String getId()       { return id; }
    public String getNombre()   { return nombre; }
    public double getPrecio()   { return precio; }
    public int getStock()       { return stock; }
}