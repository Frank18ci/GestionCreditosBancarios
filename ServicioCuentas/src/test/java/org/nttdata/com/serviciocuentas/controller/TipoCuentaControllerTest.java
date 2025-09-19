package org.nttdata.com.serviciocuentas.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaResponse;
import org.nttdata.com.serviciocuentas.service.TipoCuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoCuentaControllerTest {

    private TipoCuentaService tipoCuentaService;
    private TipoCuentaController tipoCuentaController;

    @BeforeEach
    void setUp() {
        tipoCuentaService = mock(TipoCuentaService.class);
        tipoCuentaController = new TipoCuentaController(tipoCuentaService);
    }

    @Test
    void getAllTipoCuentas_WhenNoDataExists_ShouldReturnEmptyList() {
        when(tipoCuentaService.getAllTiposCuenta()).thenReturn(Collections.emptyList());

        ResponseEntity<?> result = tipoCuentaController.getAllTipoCuentas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(((List<?>) result.getBody()).isEmpty());
        verify(tipoCuentaService, times(1)).getAllTiposCuenta();
    }

    @Test
    void getTipoCuentaById_WhenIdIsValid_ShouldReturnTipoCuenta() {
        Long id = 1L;
        TipoCuentaResponse response = new TipoCuentaResponse(1L, "Ahorro");

        when(tipoCuentaService.getTipoCuentaById(id)).thenReturn(response);

        ResponseEntity<?> result = tipoCuentaController.getTipoCuentaById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(tipoCuentaService, times(1)).getTipoCuentaById(id);
    }

    @Test
    void getTipoCuentaById_WhenIdIsInvalid_ShouldReturnNotFound() {
        Long id = 1L;

        when(tipoCuentaService.getTipoCuentaById(id)).thenThrow(new RuntimeException("Tipo de cuenta no encontrado"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tipoCuentaController.getTipoCuentaById(id));

        assertEquals("Tipo de cuenta no encontrado", ex.getMessage());
        verify(tipoCuentaService, times(1)).getTipoCuentaById(id);
    }

    @Test
    void createTipoCuenta_WhenRequestIsValid_ShouldReturnCreatedTipoCuenta() {
        TipoCuentaRequest request = new TipoCuentaRequest("Ahorro");
        TipoCuentaResponse response = new TipoCuentaResponse(1L, "Ahorro");

        when(tipoCuentaService.createTipoCuenta(request)).thenReturn(response);

        ResponseEntity<?> result = tipoCuentaController.createTipoCuenta(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(tipoCuentaService, times(1)).createTipoCuenta(request);
    }

    @Test
    void updateTipoCuenta_WhenIdAndRequestAreValid_ShouldReturnUpdatedTipoCuenta() {
        Long id = 1L;
        TipoCuentaRequest request = new TipoCuentaRequest("Corriente");
        TipoCuentaResponse response = new TipoCuentaResponse(id, "Corriente");

        when(tipoCuentaService.updateTipoCuenta(id, request)).thenReturn(response);

        ResponseEntity<?> result = tipoCuentaController.updateTipoCuenta(id, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(tipoCuentaService, times(1)).updateTipoCuenta(id, request);
    }

    @Test
    void deleteTipoCuenta_WhenIdIsValid_ShouldReturnNoContent() {
        Long id = 1L;

        doNothing().when(tipoCuentaService).deleteTipoCuenta(id);

        ResponseEntity<?> result = tipoCuentaController.deleteTipoCuenta(id);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(tipoCuentaService, times(1)).deleteTipoCuenta(id);
    }
}
