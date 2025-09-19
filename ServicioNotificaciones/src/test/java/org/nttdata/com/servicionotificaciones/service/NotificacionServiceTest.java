package org.nttdata.com.servicionotificaciones.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.NotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.NotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ResourceNotFound;
import org.nttdata.com.servicionotificaciones.model.EstadoNotificacion;
import org.nttdata.com.servicionotificaciones.model.Notificacion;
import org.nttdata.com.servicionotificaciones.model.TipoNotificacion;
import org.nttdata.com.servicionotificaciones.repository.NotificacionRepository;
import org.nttdata.com.servicionotificaciones.service.impl.NotificacionServiceImpl;
import org.nttdata.com.servicionotificaciones.util.NotificacionMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {
    @InjectMocks
    private NotificacionServiceImpl notificacionService;
    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private NotificacionMapper notificacionMapper;


    @Test
    @DisplayName("Debe retornar todas las notificaciones")
    void testObtenerTodasLasNotificaciones() {
        List<NotificacionResponse> responses = List.of(
                NotificacionResponse.builder()
                        .id(1L)
                        .mensaje("Mensaje 1")
                        .estadoNotificacion(EstadoNotificacionResponse.builder()
                                .id(1L)
                                .estado("ENVIADO")
                                .build())
                        .asunto("Asunto 1")
                        .tipoNotificacion(TipoNotificacionResponse.builder()
                                .id(1L)
                                .nombre("EMAIL")
                                .build())
                        .fechaEnvio(new Date())
                        .clienteId(1L)
                        .build(),
                NotificacionResponse.builder()
                        .id(2L)
                        .mensaje("Mensaje 2")
                        .estadoNotificacion(EstadoNotificacionResponse.builder()
                                .id(2L)
                                .estado("FALLIDO")
                                .build())
                        .asunto("Asunto 2")
                        .tipoNotificacion(TipoNotificacionResponse.builder()
                                .id(2L)
                                .nombre("SMS")
                                .build())
                        .fechaEnvio(new Date())
                        .clienteId(2L)
                        .build()
        );
        List<Notificacion> notificaciones = List.of(
                Notificacion.builder()
                        .id(1L)
                        .mensaje("Mensaje 1")
                        .estadoNotificacion(null)
                        .asunto("Asunto 1")
                        .tipoNotificacion(null)
                        .fechaEnvio(new Date())
                        .clienteId(1L)
                        .build(),
                Notificacion.builder()
                        .id(2L)
                        .mensaje("Mensaje 2")
                        .estadoNotificacion(null)
                        .asunto("Asunto 2")
                        .tipoNotificacion(null)
                        .fechaEnvio(new Date())
                        .clienteId(2L)
                        .build()
        );

        when(notificacionRepository.findAll()).thenReturn(notificaciones);
        when(notificacionMapper.toDtoList(notificaciones)).thenReturn(responses);

        List<NotificacionResponse> result = notificacionService.getAllNotificaciones();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Mensaje 1", result.getFirst().mensaje()),
                () -> assertEquals("Mensaje 2", result.get(1).mensaje())
        );
    }
    @Test
    @DisplayName("Debe retornar una notificacion por ID")
    void testObtenerNotificacionPorId() {
        Long id = 1L;
        Notificacion notificacion = Notificacion
                .builder()
                .id(id)
                .mensaje("Mensaje 1")
                .estadoNotificacion(EstadoNotificacion.builder().id(1L).estado("ENVIADO").build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacion.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();
        NotificacionResponse notificacionResponse = NotificacionResponse
                .builder()
                .id(id)
                .mensaje("Mensaje 1")
                .estadoNotificacion(EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));
        when(notificacionMapper.toDto(notificacion)).thenReturn(notificacionResponse);

        NotificacionResponse result = notificacionService.getNotificacionById(id);

        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("Mensaje 1", result.mensaje()),
                () -> assertEquals("ENVIADO", result.estadoNotificacion().estado()),
                () -> assertEquals("Asunto 1", result.asunto()),
                () -> assertEquals("EMAIL", result.tipoNotificacion().nombre()),
                () -> assertEquals(1L, result.clienteId())
        );
    }
    @Test
    @DisplayName("debe lanzar ResourceNotFound al buscar una notificacion inexistente")
    void testObtenerNotificacionPorIdNoExistente() {
        Long id = 1L;

        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> notificacionService.getNotificacionById(id));

        assertEquals("Notificacion not found with id 1", exception.getMessage());
        verify(notificacionRepository).findById(id);
    }
    @Test
    @DisplayName("debe retornar la notificacion creada")
    void testCrearNotificacion() {
        NotificacionRequest request = NotificacionRequest
                .builder()
                .mensaje("Mensaje 1")
                .estadoNotificacionId(1L)
                .asunto("Asunto 1")
                .tipoNotificacionId(1L)
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();
        Notificacion notificacion = Notificacion
                .builder()
                .id(1L)
                .mensaje("Mensaje 1")
                .estadoNotificacion(EstadoNotificacion.builder().id(1L).estado("ENVIADO").build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacion.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();
        NotificacionResponse notificacionResponse = NotificacionResponse
                .builder()
                .id(1L)
                .mensaje("Mensaje 1")
                .estadoNotificacion(EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();
        when(notificacionMapper.toEntity(request)).thenReturn(notificacion);
        when(notificacionRepository.save(notificacion)).thenReturn(notificacion);
        when(notificacionMapper.toDto(notificacion)).thenReturn(notificacionResponse);

        NotificacionResponse result = notificacionService.createNotificacion(request);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Mensaje 1", result.mensaje()),
                () -> assertEquals("ENVIADO", result.estadoNotificacion().estado()),
                () -> assertEquals("Asunto 1", result.asunto()),
                () -> assertEquals("EMAIL", result.tipoNotificacion().nombre()),
                () -> assertEquals(1L, result.clienteId())
        );
    }
    @Test
    @DisplayName("debe actualizar y retornar la notificacion actualizada")
    void testActualizarNotificacion() {
        Long id = 1L;
        NotificacionRequest request = NotificacionRequest
                .builder()
                .mensaje("Mensaje Actualizado")
                .estadoNotificacionId(2L)
                .asunto("Asunto Actualizado")
                .tipoNotificacionId(2L)
                .fechaEnvio(new Date())
                .clienteId(2L)
                .build();
        Notificacion notificacion = Notificacion
                .builder()
                .id(id)
                .mensaje("Mensaje Actualizado")
                .estadoNotificacion(EstadoNotificacion.builder().id(2L).estado("FALLIDO").build())
                .asunto("Asunto Actualizado")
                .tipoNotificacion(TipoNotificacion.builder().id(2L).nombre("SMS").build())
                .fechaEnvio(new Date())
                .clienteId(2L)
                .build();
        NotificacionResponse notificacionResponse = NotificacionResponse
                .builder()
                .id(id)
                .mensaje("Mensaje Actualizado")
                .estadoNotificacion(EstadoNotificacionResponse.builder().id(2L).estado("FALLIDO").build())
                .asunto("Asunto Actualizado")
                .tipoNotificacion(TipoNotificacionResponse.builder().id(2L).nombre("SMS").build())
                .fechaEnvio(new Date())
                .clienteId(2L)
                .build();
        when(notificacionMapper.toEntity(request)).thenReturn(notificacion);
        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(notificacion)).thenReturn(notificacion);
        when(notificacionMapper.toDto(notificacion)).thenReturn(notificacionResponse);
        NotificacionResponse result = notificacionService.updateNotificacion(id, request);
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("Mensaje Actualizado", result.mensaje()),
                () -> assertEquals("FALLIDO", result.estadoNotificacion().estado()),
                () -> assertEquals("Asunto Actualizado", result.asunto()),
                () -> assertEquals("SMS", result.tipoNotificacion().nombre()),
                () -> assertEquals(2L, result.clienteId())
        );
    }
    @Test
    @DisplayName("debe lanzar ResourceNotFound al actualizar una notificacion inexistente")
    void testActualizarNotificacionNoExistente() {
        Long id = 1L;
        NotificacionRequest request = NotificacionRequest
                .builder()
                .mensaje("Mensaje Actualizado")
                .estadoNotificacionId(2L)
                .asunto("Asunto Actualizado")
                .tipoNotificacionId(2L)
                .fechaEnvio(new Date())
                .clienteId(2L)
                .build();
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> notificacionService.updateNotificacion(id, request));
        verify(notificacionRepository).findById(id);
    }

    @Test
    @DisplayName("debe eliminar la notificacion")
    void testEliminarNotificacion() {
        Long id = 1L;
        Notificacion notificacion = Notificacion
                .builder()
                .id(id)
                .mensaje("Mensaje 1")
                .estadoNotificacion(EstadoNotificacion.builder().id(1L).estado("ENVIADO").build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacion.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();
        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));
        notificacionService.deleteNotificacion(id);

        verify(notificacionRepository).findById(id);
        verify(notificacionRepository).delete(notificacion);
    }
    @Test
    @DisplayName("debe lanzar ResourceNotFound al eliminar una notificacion inexistente")
    void testEliminarNotificacionNoExistente() {
        Long id = 1L;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFound.class, () -> notificacionService.deleteNotificacion(id));
        verify(notificacionRepository).findById(id);
    }
}
