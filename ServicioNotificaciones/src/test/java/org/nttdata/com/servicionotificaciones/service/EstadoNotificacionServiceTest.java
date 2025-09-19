package org.nttdata.com.servicionotificaciones.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ResourceNotFound;
import org.nttdata.com.servicionotificaciones.model.EstadoNotificacion;
import org.nttdata.com.servicionotificaciones.repository.EstadoNotificacionRepository;
import org.nttdata.com.servicionotificaciones.service.impl.EstadoNotificacionServiceImpl;
import org.nttdata.com.servicionotificaciones.util.EstadoNotificacionMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstadoNotificacionServiceTest {
    @InjectMocks
    private EstadoNotificacionServiceImpl estadoNotificacionService;
    @Mock
    private EstadoNotificacionRepository estadoNotificacionRepository;
    @Mock
    private EstadoNotificacionMapper estadoNotificacionMapper;

    @Test
    @DisplayName("Debe retornar todos los estados de notificaciones")
    void testObtenerTodosLosEstados() {
        List<EstadoNotificacion> estadoNotificacions = List.of(
                EstadoNotificacion.builder().id(1L).estado("ENVIADO").build(),
                EstadoNotificacion.builder().id(2L).estado("FALLIDO").build()
        );
        List<EstadoNotificacionResponse> estadoNotificacionResponses = List.of(
                EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build(),
                EstadoNotificacionResponse.builder().id(2L).estado("FALLIDO").build()
        );

        when(estadoNotificacionRepository.findAll()).thenReturn(estadoNotificacions);
        when(estadoNotificacionMapper.toDtoList(estadoNotificacions)).thenReturn(estadoNotificacionResponses);
        List<EstadoNotificacionResponse> result = estadoNotificacionService.getAllEstadosNotificacion();
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("ENVIADO", result.getFirst().estado()),
                () -> assertEquals("FALLIDO", result.get(1).estado())
        );
    }
    @Test
    @DisplayName("Debe retornar un estado de notificacion por ID")
    void testObtenerEstadoPorId() {
        Long id = 1L;
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.builder().id(id).estado("ENVIADO").build();
        EstadoNotificacionResponse estadoNotificacionResponse = EstadoNotificacionResponse.builder().id(id).estado("ENVIADO").build();

        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.of(estadoNotificacion));
        when(estadoNotificacionMapper.toDto(estadoNotificacion)).thenReturn(estadoNotificacionResponse);
        EstadoNotificacionResponse result = estadoNotificacionService.getEstadoNotificacionById(id);
        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("ENVIADO", result.estado())
        );
    }
    @Test
    @DisplayName("Debe fallar al retornar un estado de notificacion por ID inexistente")
    void testObtenerEstadoPorIdInexistente() {
        Long id = 3L;
        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoNotificacionService.getEstadoNotificacionById(id));
        assertEquals("EstadoNotificacion not found with id 3", exception.getMessage());
    }
    @Test
    @DisplayName("Debe crear un estado de notificacion")
    void testCrearEstadoNotificacion() {
        EstadoNotificacionRequest request = EstadoNotificacionRequest.builder().estado("ENVIADO").build();
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.builder().id(1L).estado("ENVIADO").build();
        EstadoNotificacionResponse estadoNotificacionResponse = EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build();

        when(estadoNotificacionRepository.save(estadoNotificacion)).thenReturn(estadoNotificacion);
        when(estadoNotificacionMapper.toDto(estadoNotificacion)).thenReturn(estadoNotificacionResponse);
        when(estadoNotificacionMapper.toEntity(request)).thenReturn(estadoNotificacion);
        EstadoNotificacionResponse result = estadoNotificacionService.createEstadoNotificacion(request);
        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("ENVIADO", result.estado())
        );
    }
    @Test
    @DisplayName("Debe actualizar un estado de notificacion")
    void testActualizarEstadoNotificacion() {
        Long id = 1L;
        EstadoNotificacionRequest request = EstadoNotificacionRequest.builder().estado("FALLIDO").build();
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.builder().id(id).estado("FALLIDO").build();
        EstadoNotificacionResponse estadoNotificacionResponse = EstadoNotificacionResponse.builder().id(id).estado("FALLIDO").build();

        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.of(estadoNotificacion));
        when(estadoNotificacionRepository.save(estadoNotificacion)).thenReturn(estadoNotificacion);
        when(estadoNotificacionMapper.toDto(estadoNotificacion)).thenReturn(estadoNotificacionResponse);
        EstadoNotificacionResponse result = estadoNotificacionService.updateEstadoNotificacion(id, request);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("FALLIDO", result.estado())
        );
    }
    @Test
    @DisplayName("Debe fallar al actualizar un estado de notificacion inexistente")
    void testActualizarEstadoNotificacionInexistente() {
        Long id = 3L;
        EstadoNotificacionRequest request = EstadoNotificacionRequest.builder().estado("FALLIDO").build();
        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoNotificacionService.updateEstadoNotificacion(id, request));
        assertEquals("EstadoNotificacion not found with id 3", exception.getMessage());
    }
    @Test
    @DisplayName("Debe eliminar un estado de notificacion")
    void testEliminarEstadoNotificacion() {
        Long id = 1L;
        EstadoNotificacion estadoNotificacion = EstadoNotificacion.builder().id(id).estado("ENVIADO").build();
        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.of(estadoNotificacion));
        assertDoesNotThrow(() -> estadoNotificacionService.deleteEstadoNotificacion(id));
    }
    @Test
    @DisplayName("Debe fallar al eliminar un estado de notificacion inexistente")
    void testEliminarEstadoNotificacionInexistente() {
        Long id = 3L;
        when(estadoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoNotificacionService.deleteEstadoNotificacion(id));
        assertEquals("EstadoNotificacion not found with id 3", exception.getMessage());
    }
}
