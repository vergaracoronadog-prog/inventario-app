package com.uniremington.inventario.repository;

import com.uniremington.inventario.model.Producto;

public interface ProductoRepository {

    void guardar(Producto producto);

    Producto buscarPorId(String id);

    boolean eliminar(String id);
}