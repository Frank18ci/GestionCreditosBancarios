package org.nttdata.com.serviciotransacciones.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionResponse;
import org.nttdata.com.serviciotransacciones.service.TipoTransaccionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoTransaccionControllerTest {

    private TipoTransaccionService tipoTransaccionService;
    private TipoTransaccionController tipoTransaccionController;

    @BeforeEach
    void setUp() {
        tipoTransaccionService = mock(TipoTransaccionService.class);
        tipoTransaccionController = new TipoTransaccionController(tipoTransaccionService);
    }

    @Test
    void getAllTipoTransacciones_WhenNoDataExists_ShouldReturnEmptyList() {
        when(tipoTransaccionService.getAllTipoTransacciones()).thenReturn(Collections.emptyList());
        ResponseEntity<?> response = tipoTransaccionController.getAllTipoTransacciones();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Iterable<?>) response.getBody()).iterator().hasNext() == false);
        verify(tipoTransaccionService, times(1)).getAllTipoTransacciones();
    }

    @Test
    void getTipoTransaccionById_WhenIdIsValid_ShouldReturnTipoTransaccion() {
        Long id = 1L;
        TipoTransaccionResponse tipoTransaccion = new TipoTransaccionResponse(1L, "Transferencia");
        when(tipoTransaccionService.getTipoTransaccionById(id)).thenReturn(tipoTransaccion);
        ResponseEntity<?> response = tipoTransaccionController.getTipoTransaccionById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tipoTransaccion, response.getBody());
        verify(tipoTransaccionService, times(1)).getTipoTransaccionById(id);
    }

    @Test
    void createTipoTransaccion_WhenRequestIsValid_ShouldReturnCreatedTipoTransaccion() {
        TipoTransaccionRequest request = new TipoTransaccionRequest("Transferencia");
        TipoTransaccionResponse createdTipoTransaccion = new TipoTransaccionResponse(1L, "Transferencia");
        when(tipoTransaccionService.createTipoTransaccion(request)).thenReturn(createdTipoTransaccion);
        ResponseEntity<?> response = tipoTransaccionController.createTipoTransaccion(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdTipoTransaccion, response.getBody());
        verify(tipoTransaccionService, times(1)).createTipoTransaccion(request);
    }

    @Test
    void updateTipoTransaccion_WhenIdAndRequestAreValid_ShouldReturnUpdatedTipoTransaccion() {
        Long id = 1L;
        TipoTransaccionRequest request = new TipoTransaccionRequest("Pago");
        TipoTransaccionResponse updatedTipoTransaccion = new TipoTransaccionResponse(1L, "Pago");
        when(tipoTransaccionService.updateTipoTransaccion(id, request)).thenReturn(updatedTipoTransaccion);
        ResponseEntity<?> response = tipoTransaccionController.updateTipoTransaccion(id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTipoTransaccion, response.getBody());
        verify(tipoTransaccionService, times(1)).updateTipoTransaccion(id, request);
    }

    @Test
    void deleteTipoTransaccion_WhenIdIsValid_ShouldReturnNoContent() {
        Long id = 1L;
        doNothing().when(tipoTransaccionService).deleteTipoTransaccion(id);
        ResponseEntity<?> response = tipoTransaccionController.deleteTipoTransaccion(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tipoTransaccionService, times(1)).deleteTipoTransaccion(id);
    }
}
