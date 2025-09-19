package org.nttdata.com.serviciocuentas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciocuentas.service.EstadoCuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstadoCuentaControllerTest {

    private EstadoCuentaService estadoCuentaService;
    private EstadoCuentaController estadoCuentaController;

    @BeforeEach
    void setUp() {
        estadoCuentaService = mock(EstadoCuentaService.class);
        estadoCuentaController = new EstadoCuentaController(estadoCuentaService);
    }

    @Test
    void getAllEstadoCuentas_WhenNoDataExists_ShouldReturnEmptyList() {
        when(estadoCuentaService.getAllEstadosCuenta()).thenReturn(Collections.emptyList());

        ResponseEntity<?> result = estadoCuentaController.getAllEstadoCuentas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(((List<?>) result.getBody()).isEmpty());
        verify(estadoCuentaService, times(1)).getAllEstadosCuenta();
    }

    @Test
    void getEstadoCuentaById_WhenIdIsValid_ShouldReturnEstadoCuenta() {
        Long id = 1L;
        EstadoCuentaResponse response = new EstadoCuentaResponse(1L, "Activo");

        when(estadoCuentaService.getEstadoCuentaById(id)).thenReturn(response);

        ResponseEntity<?> result = estadoCuentaController.getEstadoCuentaById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(estadoCuentaService, times(1)).getEstadoCuentaById(id);
    }

    @Test
    void getEstadoCuentaById_WhenIdIsInvalid_ShouldReturnNotFound() {
        Long id = 1L;

        when(estadoCuentaService.getEstadoCuentaById(id)).thenThrow(new RuntimeException("Estado de cuenta no encontrado"));

        ResponseEntity<?> result = estadoCuentaController.getEstadoCuentaById(id);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Estado de cuenta no encontrado", result.getBody());
        verify(estadoCuentaService, times(1)).getEstadoCuentaById(id);
    }

    @Test
    void createEstadoCuenta_WhenRequestIsValid_ShouldReturnCreatedEstadoCuenta() {
        EstadoCuentaRequest request = new EstadoCuentaRequest("Activo");
        EstadoCuentaResponse response = new EstadoCuentaResponse(1L, "Activo");

        when(estadoCuentaService.saveEstadoCuenta(request)).thenReturn(response);

        ResponseEntity<?> result = estadoCuentaController.createEstadoCuenta(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(estadoCuentaService, times(1)).saveEstadoCuenta(request);
    }

    @Test
    void updateEstadoCuenta_WhenIdAndRequestAreValid_ShouldReturnUpdatedEstadoCuenta() {
        Long id = 1L;
        EstadoCuentaRequest request = new EstadoCuentaRequest("Actualizado");
        EstadoCuentaResponse response = new EstadoCuentaResponse(id, "Actualizado");

        when(estadoCuentaService.updateEstadoCuenta(id, request)).thenReturn(response);

        ResponseEntity<?> result = estadoCuentaController.updateEstadoCuenta(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(estadoCuentaService, times(1)).updateEstadoCuenta(id, request);
    }

    @Test
    void deleteEstadoCuenta_WhenIdIsValid_ShouldReturnNoContent() {
        Long id = 1L;

        doNothing().when(estadoCuentaService).deleteEstadoCuenta(id);

        ResponseEntity<?> result = estadoCuentaController.deleteEstadoCuenta(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(estadoCuentaService, times(1)).deleteEstadoCuenta(id);
    }
}
