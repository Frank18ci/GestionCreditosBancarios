package org.nttdata.com.servicionotificaciones.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ResourceNotFound;
import org.nttdata.com.servicionotificaciones.model.TipoNotificacion;
import org.nttdata.com.servicionotificaciones.repository.TipoNotificacionRepository;
import org.nttdata.com.servicionotificaciones.service.impl.TipoNotificacionServiceImpl;
import org.nttdata.com.servicionotificaciones.util.TipoNotificacionMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoNotificacionServiceTest {
    @InjectMocks
    private TipoNotificacionServiceImpl tipoNotificacionService;
    @Mock
    private TipoNotificacionRepository tipoNotificacionRepository;
    @Mock
    private TipoNotificacionMapper tipoNotificacionMapper;

    @Test
    @DisplayName("Debe retornar todos los tipos de notificaciones")
    void testObtenerTodosLosTipos() {
        List<TipoNotificacion> tipoNotificacions = List.of(
                TipoNotificacion.builder().id(1L).nombre("EMAIL").build(),
                TipoNotificacion.builder().id(2L).nombre("SMS").build()
        );
        List<TipoNotificacionResponse> tipoNotificacionResponses = List.of(
                TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build(),
                TipoNotificacionResponse.builder().id(2L).nombre("SMS").build()
        );
        when(tipoNotificacionRepository.findAll()).thenReturn(tipoNotificacions);
        when(tipoNotificacionMapper.toDtoList(tipoNotificacions)).thenReturn(tipoNotificacionResponses);
        List<TipoNotificacionResponse> result = tipoNotificacionService.getAllTiposNotificacion();
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("EMAIL", result.getFirst().nombre()),
                () -> assertEquals("SMS", result.get(1).nombre())
        );

        verify(tipoNotificacionRepository).findAll();
        verify(tipoNotificacionMapper).toDtoList(tipoNotificacions);
    }
    @Test
    @DisplayName("Debe retornar un tipo de notificacion por ID")
    void testObtenerTipoPorId() {
        Long id = 1L;
        TipoNotificacion tipoNotificacion = TipoNotificacion.builder().id(id).nombre("EMAIL").build();
        TipoNotificacionResponse tipoNotificacionResponse = TipoNotificacionResponse.builder().id(id).nombre("EMAIL").build();
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.of(tipoNotificacion));
        when(tipoNotificacionMapper.toDto(tipoNotificacion)).thenReturn(tipoNotificacionResponse);
        TipoNotificacionResponse result = tipoNotificacionService.getTipoNotificacionById(id);
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("EMAIL", result.nombre())
        );
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el tipo de notificacion no existe")
    void testObtenerTipoPorIdNoExiste() {
        Long id = 1L;
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> tipoNotificacionService.getTipoNotificacionById(id));
        assertEquals("Tipo de notificación no encontrado con id: 1", exception.getMessage());
    }
    @Test
    @DisplayName("Debe crear un tipo de notificacion")
    void testCrearTipoNotificacion() {
        TipoNotificacionRequest request = TipoNotificacionRequest.builder().nombre("EMAIL").build();
        TipoNotificacion tipoNotificacion = TipoNotificacion.builder().id(1L).nombre("EMAIL").build();
        TipoNotificacionResponse tipoNotificacionResponse = TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build();
        when(tipoNotificacionMapper.toEntity(request)).thenReturn(tipoNotificacion);
        when(tipoNotificacionRepository.save(tipoNotificacion)).thenReturn(tipoNotificacion);
        when(tipoNotificacionMapper.toDto(tipoNotificacion)).thenReturn(tipoNotificacionResponse);
        TipoNotificacionResponse result = tipoNotificacionService.saveTipoNotificacion(request);
        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("EMAIL", result.nombre())
        );
    }
    @Test
    @DisplayName("Debe actualizar un tipo de notificacion")
    void testActualizarTipoNotificacion() {
        Long id = 1L;
        TipoNotificacionRequest request = TipoNotificacionRequest.builder().nombre("SMS").build();
        TipoNotificacion tipoNotificacion = TipoNotificacion.builder().id(id).nombre("EMAIL").build();
        TipoNotificacion tipoNotificacionUpdated = TipoNotificacion.builder().id(id).nombre("SMS").build();
        TipoNotificacionResponse tipoNotificacionResponse = TipoNotificacionResponse.builder().id(id).nombre("SMS").build();
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.of(tipoNotificacion));
        when(tipoNotificacionRepository.save(tipoNotificacion)).thenReturn(tipoNotificacionUpdated);
        when(tipoNotificacionMapper.toDto(tipoNotificacionUpdated)).thenReturn(tipoNotificacionResponse);
        TipoNotificacionResponse result = tipoNotificacionService.updateTipoNotificacion(id, request);
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("SMS", result.nombre())
        );
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el tipo de notificacion no existe al actualizar")
    void testActualizarTipoNotificacionNoExiste() {
        Long id = 1L;
        TipoNotificacionRequest request = TipoNotificacionRequest.builder().nombre("SMS").build();
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> tipoNotificacionService.updateTipoNotificacion(id, request));
        assertEquals("Tipo de notificación no encontrado con id: 1", exception.getMessage());
    }
    @Test
    @DisplayName("Debe eliminar un tipo de notificacion")
    void testEliminarTipoNotificacion() {
        Long id = 1L;
        TipoNotificacion tipoNotificacion = TipoNotificacion.builder().id(id).nombre("EMAIL").build();
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.of(tipoNotificacion));
        assertDoesNotThrow(() -> tipoNotificacionService.deleteTipoNotificacion(id));
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el tipo de notificacion no existe al eliminar")
    void testEliminarTipoNotificacionNoExiste() {
        Long id = 1L;
        when(tipoNotificacionRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> tipoNotificacionService.deleteTipoNotificacion(id));
        assertEquals("Tipo de notificación no encontrado con id: 1", exception.getMessage());
    }
}
