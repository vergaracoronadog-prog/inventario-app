package com.uniremington.inventario.service;

import com.uniremington.inventario.model.Producto;
import com.uniremington.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventarioService: lógica de negocio con dependencias simuladas")
class InventarioServiceTest {

    @Mock
    private ProductoRepository repositorio;

    @Mock
    private NotificadorService notificador;

    @InjectMocks
    private InventarioService servicio;

    private Producto productoEjemplo;

    @BeforeEach
    void preparar() {
        productoEjemplo = new Producto("P001", "Laptop HP", 1500.0, 5);
    }

    @Test
    @DisplayName("Registrar producto nuevo: debe llamar a repositorio.guardar()")
    void registrarProducto_valido() {
        when(repositorio.buscarPorId("P001")).thenReturn(null);

        servicio.registrarProducto(productoEjemplo);

        verify(repositorio, times(1)).guardar(productoEjemplo);
    }

    @Test
    @DisplayName("Vender producto con stock suficiente: retorna stock restante")
    void venderProducto_valido() {
        when(repositorio.buscarPorId("P001")).thenReturn(productoEjemplo);

        int stockRestante = servicio.venderProducto("P001", 3);

        assertEquals(2, stockRestante);
        verify(repositorio, times(1)).guardar(productoEjemplo);
        verify(notificador, never()).notificar(anyString(), anyString());
    }

    @Test
    @DisplayName("Buscar producto existente: retorna el producto")
    void buscarProducto_valido() {
        when(repositorio.buscarPorId("P001")).thenReturn(productoEjemplo);

        Producto encontrado = servicio.buscarProducto("P001");

        assertNotNull(encontrado);
        assertEquals("P001", encontrado.getId());
        verify(repositorio, times(1)).buscarPorId("P001");
    }

    @Test
    @DisplayName("Registrar producto null: lanza IllegalArgumentException")
    void registrarProducto_null_invalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarProducto(null)
        );
        verify(repositorio, never()).guardar(any());
    }

    @Test
    @DisplayName("Registrar producto duplicado: lanza IllegalStateException")
    void registrarProducto_duplicado_invalido() {
        when(repositorio.buscarPorId("P001")).thenReturn(productoEjemplo);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.registrarProducto(productoEjemplo)
        );
        verify(repositorio, never()).guardar(any());
    }

    @Test
    @DisplayName("Vender producto inexistente: lanza IllegalStateException")
    void venderProducto_inexistente_invalido() {
        when(repositorio.buscarPorId("X999")).thenReturn(null);

        assertThrows(
                IllegalStateException.class,
                () -> servicio.venderProducto("X999", 1)
        );
        verify(repositorio, never()).guardar(any());
        verify(notificador, never()).notificar(anyString(), anyString());
    }

    @Test
    @DisplayName("Vender con cantidad cero o negativa: lanza IllegalArgumentException")
    void venderProducto_cantidadInvalida_invalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.venderProducto("P001", 0)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.venderProducto("P001", -5)
        );
    }

    @Test
    @DisplayName("Vender más del stock disponible: lanza IllegalStateException")
    void venderProducto_stockInsuficiente_invalido() {

        assertThrows(
                IllegalStateException.class,
                () -> servicio.venderProducto("P001", 100)
        );
    }

    @Test
    @DisplayName("Vender la totalidad del stock: debe llamar al notificador (límite stock = 0)")
    void venderProducto_todoElStock_limite() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 3);
        when(repositorio.buscarPorId("P001")).thenReturn(producto);
        when(notificador.notificar(anyString(), anyString())).thenReturn(true);

        int stockRestante = servicio.venderProducto("P001", 3);

        assertEquals(0, stockRestante);
        verify(repositorio, times(1)).guardar(producto);
        verify(notificador, times(1)).notificar(eq("admin@inventario.com"), contains("agotado"));
    }

    @Test
    @DisplayName("Buscar producto con id vacío: lanza IllegalArgumentException (límite cadena)")
    void buscarProducto_idVacio_limite() {
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarProducto("")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.buscarProducto("   ")
        );
        verify(repositorio, never()).buscarPorId(anyString());
    }

    @Test
    @DisplayName("Buscar producto que no existe: retorna null sin error (límite ausencia)")
    void buscarProducto_noExistente_limite() {
        when(repositorio.buscarPorId("ZZZ")).thenReturn(null);

        Producto resultado = servicio.buscarProducto("ZZZ");

        assertNull(resultado);
        verify(repositorio, times(1)).buscarPorId("ZZZ");
    }

    @Test
    @DisplayName("Vender 1 unidad cuando el stock es 1: agota y notifica (límite unitario)")
    void venderProducto_ultimaUnidad_limite() {
        Producto producto = new Producto("P001", "Mouse", 25.0, 1);
        when(repositorio.buscarPorId("P001")).thenReturn(producto);

        int stockRestante = servicio.venderProducto("P001", 1);

        assertEquals(0, stockRestante);
        verify(notificador, times(1)).notificar(anyString(), anyString());
    }
}