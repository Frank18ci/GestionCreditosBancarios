package org.nttdata.com.serviciocuentas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciocuentas.dto.CuentaRequest;
import org.nttdata.com.serviciocuentas.dto.CuentaResponse;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaResponse;
import org.nttdata.com.serviciocuentas.service.CuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaControllerTest {

    private CuentaService cuentaService;
    private CuentaController cuentaController;

    @BeforeEach
    void setUp() {
        cuentaService = mock(CuentaService.class);
        cuentaController = new CuentaController(cuentaService);
    }

    @Test
    void crearCuenta_WhenRequestIsValid_ShouldReturnCreatedCuenta() {
        CuentaRequest request = new CuentaRequest(1L, 1L, 1L, BigDecimal.TEN);
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(1L, 1L, tipoCuenta, estadoCuenta, BigDecimal.TEN);

        when(cuentaService.crearCuenta(request)).thenReturn(response);

        ResponseEntity<?> result = cuentaController.crearCuenta(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(cuentaService, times(1)).crearCuenta(request);
    }

    @Test
    void obtenerCuenta_WhenIdIsValid_ShouldReturnCuenta() {
        Long id = 1L;
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(id, 1L, tipoCuenta, estadoCuenta, BigDecimal.TEN);

        when(cuentaService.obtenerCuentaPorId(id)).thenReturn(response);

        ResponseEntity<?> result = cuentaController.obtenerCuenta(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(cuentaService, times(1)).obtenerCuentaPorId(id);
    }

    @Test
    void obtenerCuenta_WhenIdIsInvalid_ShouldReturnNotFound() {
        Long id = 1L;

        when(cuentaService.obtenerCuentaPorId(id)).thenThrow(new RuntimeException("Cuenta no encontrada"));

        ResponseEntity<?> result = cuentaController.obtenerCuenta(id);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Cuenta no encontrada", result.getBody());
        verify(cuentaService, times(1)).obtenerCuentaPorId(id);
    }

    @Test
    void obtenerTodasCuentas_WhenNoDataExists_ShouldReturnEmptyList() {
        when(cuentaService.obtenerTodasCuentas()).thenReturn(Collections.emptyList());

        ResponseEntity<?> result = cuentaController.obtenerTodasCuentas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List<?>) result.getBody()).isEmpty());
        verify(cuentaService, times(1)).obtenerTodasCuentas();
    }

    @Test
    void actualizarSaldo_WhenIdAndSaldoAreValid_ShouldReturnUpdatedCuenta() {
        Long id = 1L;
        BigDecimal saldo = BigDecimal.valueOf(100);
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(id, 1L, tipoCuenta, estadoCuenta, saldo);

        when(cuentaService.actualizarSaldo(id, saldo)).thenReturn(response);

        ResponseEntity<?> result = cuentaController.actualizarSaldo(id, saldo);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(cuentaService, times(1)).actualizarSaldo(id, saldo);
    }

    @Test
    void eliminarCuenta_WhenIdIsValid_ShouldReturnNoContent() {
        Long id = 1L;

        doNothing().when(cuentaService).eliminarCuenta(id);

        ResponseEntity<?> result = cuentaController.eliminarCuenta(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(cuentaService, times(1)).eliminarCuenta(id);
    }
}
