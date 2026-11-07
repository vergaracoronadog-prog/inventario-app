 package com.uniremington.inventario.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Producto: validaciones de creación y stock")
class ProductoTest {

    @Test
    @DisplayName("Crear producto con datos válidos")
    void crearProducto_valido() {
        String id = "P001";
        String nombre = "Laptop HP";
        double precio = 1500.0;
        int stock = 10;

        Producto producto = new Producto(id, nombre, precio, stock);

        assertNotNull(producto);
        assertEquals(id, producto.getId());
        assertEquals(nombre, producto.getNombre());
        assertEquals(precio, producto.getPrecio());
        assertEquals(stock, producto.getStock());
    }

    @Test
    @DisplayName("Reducir stock con cantidad válida")
    void reducirStock_valido() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 5);

        producto.reducirStock(3);

        assertEquals(2, producto.getStock());
    }

    @Test
    @DisplayName("Crear producto con id nulo debe lanzar excepción")
    void crearProducto_idNulo_invalido() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Producto(null, "Mouse", 25.0, 5)
        );
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    @DisplayName("Crear producto con nombre vacío debe lanzar excepción")
    void crearProducto_nombreVacio_invalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Producto("P001", "   ", 25.0, 5)
        );
    }

    @Test
    @DisplayName("Crear producto con precio negativo debe lanzar excepción")
    void crearProducto_precioNegativo_invalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Producto("P001", "Mouse", -10.0, 5)
        );
    }

    @Test
    @DisplayName("Crear producto con stock negativo debe lanzar excepción")
    void crearProducto_stockNegativo_invalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Producto("P001", "Mouse", 25.0, -1)
        );
    }

    @Test
    @DisplayName("Reducir más stock del disponible debe lanzar IllegalStateException")
    void reducirStock_insuficiente_invalido() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 2);
        assertThrows(
                IllegalStateException.class,
                () -> producto.reducirStock(5)
        );
    }

    @Test
    @DisplayName("Crear producto con precio mínimo positivo (0.01)")
    void crearProducto_precioMinimo_limite() {
        Producto producto = new Producto("P001", "Clip", 0.01, 100);
        assertEquals(0.01, producto.getPrecio(), 0.0001);
    }

    @Test
    @DisplayName("Crear producto con stock cero (límite inferior)")
    void crearProducto_stockCero_limite() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 0);
        assertEquals(0, producto.getStock());
    }

    @Test
    @DisplayName("Reducir stock hasta dejarlo exactamente en cero")
    void reducirStock_aCero_limite() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 3);

        producto.reducirStock(3);

        assertEquals(0, producto.getStock());
    }

    @Test
    @DisplayName("Reducir stock con cantidad cero debe lanzar excepción (límite inferior no válido)")
    void reducirStock_cantidadCero_limite() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 3);
        assertThrows(
                IllegalArgumentException.class,
                () -> producto.reducirStock(0)
        );
    }
}