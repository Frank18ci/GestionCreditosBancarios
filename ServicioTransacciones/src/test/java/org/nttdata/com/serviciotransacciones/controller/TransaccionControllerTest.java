package org.nttdata.com.serviciotransacciones.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionResponse;
import org.nttdata.com.serviciotransacciones.dto.TransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TransaccionResponse;
import org.nttdata.com.serviciotransacciones.service.TransaccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionControllerTest {

    private TransaccionService transaccionService;
    private TransaccionController transaccionController;

    @BeforeEach
    void setUp() {
        transaccionService = mock(TransaccionService.class);
        transaccionController = new TransaccionController(transaccionService);
    }

    @Test
    void getAllTransacciones_WhenNoDataExists_ShouldReturnEmptyList() {
        when(transaccionService.getAllTransacciones()).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = transaccionController.getAllTransacciones();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
        verify(transaccionService, times(1)).getAllTransacciones();
    }

    @Test
    void getTransaccionesByCuentaId_WhenCuentaIdIsValid_ShouldReturnTransacciones() {
        Long cuentaId = 1L;
        TipoTransaccionResponse tipoTransaccion = new TipoTransaccionResponse(1L, "Transferencia");
        TransaccionResponse transaccion = new TransaccionResponse(1L, cuentaId, tipoTransaccion, BigDecimal.TEN, new Date(), "detalle");
        List<TransaccionResponse> transacciones = List.of(transaccion);
        when(transaccionService.getTransaccionesByCuentaId(cuentaId)).thenReturn(transacciones);
        ResponseEntity<?> response = transaccionController.getTransaccionesByCuentaId(cuentaId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transacciones, response.getBody());
        verify(transaccionService, times(1)).getTransaccionesByCuentaId(cuentaId);
    }

    @Test
    void getTransaccionById_WhenIdIsValid_ShouldReturnTransaccion() {
        Long id = 1L;
        TipoTransaccionResponse tipoTransaccion = new TipoTransaccionResponse(1L, "Transferencia");
        TransaccionResponse transaccion = new TransaccionResponse(id, 1L, tipoTransaccion, BigDecimal.TEN, new Date(), "detalle");
        when(transaccionService.getTransaccionById(id)).thenReturn(transaccion);
        ResponseEntity<?> response = transaccionController.getTransaccionById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaccion, response.getBody());
        verify(transaccionService, times(1)).getTransaccionById(id);
    }

    @Test
    void createTransaccion_WhenRequestIsValid_ShouldReturnCreatedTransaccion() {
        TransaccionRequest request = new TransaccionRequest(1L, 1L, BigDecimal.valueOf(100.0), new Date(), "Depósito inicial");
        TipoTransaccionResponse tipoTransaccion = new TipoTransaccionResponse(1L, "DEPOSITO");
        TransaccionResponse createdTransaccion = new TransaccionResponse(1L, 1L, tipoTransaccion, BigDecimal.valueOf(100.0), new Date(), "Depósito inicial");
        when(transaccionService.createTransaccion(request)).thenReturn(createdTransaccion);
        ResponseEntity<?> response = transaccionController.createTransaccion(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdTransaccion, response.getBody());
        verify(transaccionService, times(1)).createTransaccion(request);
    }

    @Test
    void updateTransaccion_WhenIdAndRequestAreValid_ShouldReturnUpdatedTransaccion() {
        Long id = 1L;
        TransaccionRequest request = new TransaccionRequest(1L, 1L, BigDecimal.valueOf(200.0), new Date(), "Retiro de efectivo");
        TipoTransaccionResponse tipoTransaccion = new TipoTransaccionResponse(1L, "RETIRO");
        TransaccionResponse updatedTransaccion = new TransaccionResponse(id, 1L, tipoTransaccion, BigDecimal.valueOf(200.0), new Date(), "Retiro de efectivo");
        when(transaccionService.updateTransaccion(id, request)).thenReturn(updatedTransaccion);
        ResponseEntity<?> response = transaccionController.updateTransaccion(id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTransaccion, response.getBody());
        verify(transaccionService, times(1)).updateTransaccion(id, request);
    }

    @Test
    void deleteTransaccion_WhenIdIsValid_ShouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(transaccionService).deleteTransaccion(id);
        ResponseEntity<?> response = transaccionController.deleteTransaccion(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transaccionService, times(1)).deleteTransaccion(id);
    }
}
