package org.nttdata.com.servicioclientes.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioclientes.dto.EstadoClienteRequest;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.model.EstadoCliente;
import org.nttdata.com.servicioclientes.repository.EstadoClienteRepository;
import org.nttdata.com.servicioclientes.service.impl.EstadoClienteServiceImpl;
import org.nttdata.com.servicioclientes.util.EstadoClienteMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstadoClienteServiceTest {

    @InjectMocks
    private EstadoClienteServiceImpl estadoClienteService;
    @Mock
    private EstadoClienteRepository estadoClienteRepository;
    @Mock
    private EstadoClienteMapper estadoClienteMapper;


    @Test
    @DisplayName("Debe retornar todos los estados de clientes")
    void testObtenerTodosLosEstados() {
        List<EstadoCliente> estadoClientes = List.of(
                EstadoCliente.builder().id(1L).estado("Activo").build(),
                EstadoCliente.builder().id(2L).estado("Inactivo").build()
        );

        List<EstadoClienteResponse> estadoClientesResponse = List.of(
                EstadoClienteResponse.builder().id(1L).estado("Activo").build(),
                EstadoClienteResponse.builder().id(2L).estado("Inactivo").build()
        );

        when(estadoClienteRepository.findAll()).thenReturn(estadoClientes);
        when(estadoClienteMapper.toDtoList(estadoClientes)).thenReturn(estadoClientesResponse);

        List<EstadoClienteResponse> result = estadoClienteService.obtenerTodosLosEstados();


        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Activo", result.getFirst().estado()),
                () -> assertEquals("Inactivo", result.get(1).estado())
        );
    }
    @Test
    @DisplayName("Debe retornar un estado de cliente por ID")
    void obtenerUnEstadoClienteTest() {
        Long estadoId = 1L;
        EstadoCliente estadoCliente = EstadoCliente.builder().id(estadoId).estado("Activo").build();
        EstadoClienteResponse estadoClienteResponse = EstadoClienteResponse.builder().id(estadoId).estado("Activo").build();

        when(estadoClienteRepository.findById(1L)).thenReturn(java.util.Optional.of(estadoCliente));
        when(estadoClienteMapper.toDto(estadoCliente)).thenReturn(estadoClienteResponse);

        EstadoClienteResponse result = estadoClienteService.obtenerEstadoPorId(estadoId);

        assertAll(
                () -> assertEquals(estadoId, result.id()),
                () -> assertEquals("Activo", result.estado())
        );
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el estado no existe")
    void obtenerUnEstadoClienteNoExistenteTest() {
        Long estadoId = 99L;
        when(estadoClienteRepository.findById(estadoId)).thenReturn(Optional.empty());


        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> estadoClienteService.obtenerEstadoPorId(estadoId));
        assertEquals("Estado de cliente no encontrado con id: 99", exception.getMessage());

        verify(estadoClienteMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("Debe crear un estado de cliente")
    void crearClienteTest(){
        Long estadoId = 1L;
        EstadoCliente estadoCliente = EstadoCliente.builder().id(estadoId).estado("Activo").build();
        EstadoClienteRequest estadoClienteRequest = EstadoClienteRequest.builder().estado("Activo").build();
        EstadoClienteResponse estadoClienteResponse = EstadoClienteResponse.builder().id(estadoId).estado("Activo").build();

        when(estadoClienteRepository.save(estadoCliente)).thenReturn(estadoCliente);

        when(estadoClienteMapper.toDto(estadoCliente)).thenReturn(estadoClienteResponse);
        when(estadoClienteMapper.toEntity(estadoClienteRequest)).thenReturn(estadoCliente);

        EstadoClienteResponse result = estadoClienteService.crearEstado(estadoClienteRequest);

        assertAll(
                () -> assertEquals(estadoId, result.id()),
                () -> assertEquals("Activo", result.estado())
        );
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el estado no existe al actualizar")
    void actualizarClienteNoExistenteTest() {
        Long estadoId = 99L;
        EstadoClienteRequest estadoClienteRequest = EstadoClienteRequest.builder().estado("Inactivo").build();
        when(estadoClienteRepository.findById(estadoId)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> estadoClienteService.actualizarEstado(estadoId, estadoClienteRequest));
        assertEquals("Estado de cliente no encontrado con id: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Debe actualizar un estado de cliente")
    void actualizarClienteTest() {
        Long estadoId = 1L;
        // EstadoCliente antes de la actualización
        EstadoCliente estadoCliente = EstadoCliente.builder().id(estadoId).estado("Activo").build();
        // Datos para actualizar el estado del cliente
        EstadoClienteRequest estadoClienteRequest = EstadoClienteRequest.builder().estado("Inactivo").build();
        // EstadoCliente después de la actualización
        EstadoCliente estadoClienteUpdated = EstadoCliente.builder().id(estadoId).estado("Inactivo").build();
        // Respuesta esperada después de la actualización
        EstadoClienteResponse estadoClienteResponse = EstadoClienteResponse.builder().id(estadoId).estado("Inactivo").build();

        // Mockeos
        when(estadoClienteRepository.findById(estadoId)).thenReturn(Optional.of(estadoCliente));

        when(estadoClienteRepository.save(estadoCliente)).thenReturn(estadoClienteUpdated);

        when(estadoClienteMapper.toDto(estadoClienteUpdated)).thenReturn(estadoClienteResponse);

        EstadoClienteResponse result = estadoClienteService.actualizarEstado(estadoId, estadoClienteRequest);

        assertAll(
                () -> assertEquals(estadoId, result.id()),
                () -> assertEquals("Inactivo", result.estado())
        );
    }
    @Test
    @DisplayName("Debe eliminar un estado de cliente")
    void eliminarClienteTest() {
        Long estadoId = 1L;
        EstadoCliente estadoCliente = EstadoCliente.builder().id(estadoId).estado("Activo").build();

        when(estadoClienteRepository.findById(estadoId)).thenReturn(Optional.of(estadoCliente));

        estadoClienteService.eliminarEstado(estadoId);

        verify(estadoClienteRepository, times(1)).delete(estadoCliente);
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el estado no existe")
    void eliminarClienteNoExistenteTest() {
        Long estadoId = 99L;
        when(estadoClienteRepository.findById(estadoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class,
                () -> estadoClienteService.eliminarEstado(estadoId));

        verify(estadoClienteRepository, never()).delete(any());
    }


}
