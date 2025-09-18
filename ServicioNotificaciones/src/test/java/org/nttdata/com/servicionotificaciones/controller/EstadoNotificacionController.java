package org.nttdata.com.servicionotificaciones.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@InjectMocks()
public class EstadoNotificacionController {

    @Test
    public void getAll_CuandoExistenEstados_DeberiaRetornarListaEstados() {
        when(estadoNotificacionService.getAllEstadosNotificacion()).thenReturn(List.of(new EstadoNotificacionResponse()));
        ResponseEntity<?> response = estadoNotificacionController.getAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getById_CuandoIdEsValido_DeberiaRetornarEstado() {
        Long id = 1L;
        when(estadoNotificacionService.getEstadoNotificacionById(id)).thenReturn(new EstadoNotificacionResponse());
        ResponseEntity<?> response = estadoNotificacionController.getById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void getById_CuandoIdNoExiste_DeberiaLanzarExcepcion() {
        Long id = 99L;
        when(estadoNotificacionService.getEstadoNotificacionById(id)).thenThrow(new EntityNotFoundException("Estado no encontrado"));
        assertThrows(EntityNotFoundException.class, () -> estadoNotificacionController.getById(id));
    }

    @Test
    public void create_CuandoRequestEsValido_DeberiaRetornarEstadoCreado() {
        EstadoNotificacionRequest request = new EstadoNotificacionRequest();
        when(estadoNotificacionService.createEstadoNotificacion(request)).thenReturn(new EstadoNotificacionResponse());
        ResponseEntity<?> response = estadoNotificacionController.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void update_CuandoIdYRequestSonValidos_DeberiaRetornarEstadoActualizado() {
        Long id = 1L;
        EstadoNotificacionRequest request = new EstadoNotificacionRequest();
        when(estadoNotificacionService.updateEstadoNotificacion(id, request)).thenReturn(new EstadoNotificacionResponse());
        ResponseEntity<?> response = estadoNotificacionController.update(id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void delete_CuandoIdEsValido_DeberiaEliminarEstado() {
        Long id = 1L;
        doNothing().when(estadoNotificacionService).deleteEstadoNotificacion(id);
        ResponseEntity<?> response = estadoNotificacionController.delete(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
