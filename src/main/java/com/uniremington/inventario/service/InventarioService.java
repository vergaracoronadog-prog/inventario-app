package com.uniremington.inventario.service;

import com.uniremington.inventario.model.Producto;
import com.uniremington.inventario.repository.ProductoRepository;

public class InventarioService {

    private final ProductoRepository repositorio;
    private final NotificadorService notificador;

    public InventarioService(ProductoRepository repositorio, NotificadorService notificador) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser null");
        }
        if (notificador == null) {
            throw new IllegalArgumentException("El notificador no puede ser null");
        }
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public void registrarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        if (repositorio.buscarPorId(producto.getId()) != null) {
            throw new IllegalStateException("Ya existe un producto con id: " + producto.getId());
        }
        repositorio.guardar(producto);
    }

    public int venderProducto(String id, int cantidad) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        Producto producto = repositorio.buscarPorId(id);
        if (producto == null) {
            throw new IllegalStateException("Producto no encontrado: " + id);
        }

        producto.reducirStock(cantidad);
        repositorio.guardar(producto);

        if (producto.getStock() == 0) {
            notificador.notificar("admin@inventario.com",
                    "El producto " + producto.getNombre() + " se ha agotado.");
        }
        return producto.getStock();
    }

    public Producto buscarProducto(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id no puede ser nulo ni vacío");
        }
        return repositorio.buscarPorId(id);
    }
}