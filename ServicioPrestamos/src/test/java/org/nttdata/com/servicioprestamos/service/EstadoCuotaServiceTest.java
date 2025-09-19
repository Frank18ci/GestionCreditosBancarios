package org.nttdata.com.servicioprestamos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaRequest;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoCuota;
import org.nttdata.com.servicioprestamos.repository.EstadoCuotaRepository;
import org.nttdata.com.servicioprestamos.util.EstadoCuotaMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstadoCuotaServiceTest {
    @InjectMocks
    private EstadoCuotaServiceImpl estadoCuotaService;
    @Mock
    private EstadoCuotaMapper estadoCuotaMapper;
    @Mock
    private EstadoCuotaRepository estadoCuotaRepository;

    @Test
    @DisplayName("Prueba de listar estados de cuota")
    void getAllEstadosCuotaTest() {
        List<EstadoCuota> estadoCuotas = List.of(
                EstadoCuota.builder().id(1L).nombre("PENDIENTE").build(),
                EstadoCuota.builder().id(2L).nombre("PAGADO").build()
        );
        List<EstadoCuotaResponse> estadoCuotaResponses = List.of(
                EstadoCuotaResponse.builder().id(1L).nombre("PENDIENTE").build(),
                EstadoCuotaResponse.builder().id(2L).nombre("PAGADO").build()
        );
        when(estadoCuotaRepository.findAll()).thenReturn(estadoCuotas);
        when(estadoCuotaMapper.toDtoList(estadoCuotas)).thenReturn(estadoCuotaResponses);
        List<EstadoCuotaResponse> result = estadoCuotaService.getAllEstadosCuota();
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("PENDIENTE", result.getFirst().nombre()),
                () -> assertEquals("PAGADO", result.get(1).nombre())
        );
    }
    @Test
    @DisplayName("Prueba de obtener estado de cuota por ID")
    void getEstadoCuotaByIdTest() {
        Long estadoCuotaId = 1L;
        EstadoCuota estadoCuota = EstadoCuota.builder().id(estadoCuotaId).nombre("PENDIENTE").build();
        EstadoCuotaResponse estadoCuotaResponse = EstadoCuotaResponse.builder().id(estadoCuotaId).nombre("PENDIENTE").build();

        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(java.util.Optional.of(estadoCuota));
        when(estadoCuotaMapper.toDto(estadoCuota)).thenReturn(estadoCuotaResponse);

        EstadoCuotaResponse result = estadoCuotaService.getEstadoCuotaById(estadoCuotaId);

        assertAll(
                () -> assertEquals(estadoCuotaId, result.id()),
                () -> assertEquals("PENDIENTE", result.nombre())
        );
    }
    @Test
    @DisplayName("Prueba de obtener estado de cuota ResourceNotFound")
    void getEstadoCuotaByIdResourceNotFoundTest() {
        Long estadoCuotaId = 1L;
        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> estadoCuotaService.getEstadoCuotaById(estadoCuotaId));
        assertEquals("Estado de cuota no encontrado con id: " + estadoCuotaId, exceoption.getMessage());
        verify(estadoCuotaRepository).findById(estadoCuotaId);
    }
    @Test
    @DisplayName("prueba de crear estado de cuota")
    void saveEstadoCuotaTest() {
        EstadoCuotaRequest estadoCuotaRequest = EstadoCuotaRequest.builder()
                .nombre("PENDIENTE")
                .build();
        EstadoCuota estadoCuota = EstadoCuota.builder().id(1L).nombre("PENDIENTE").build();

        EstadoCuotaResponse estadoCuotaResponse = EstadoCuotaResponse.builder().id(1L).nombre("PENDIENTE").build();

        when(estadoCuotaMapper.toEntity(estadoCuotaRequest)).thenReturn(estadoCuota);
        when(estadoCuotaRepository.save(estadoCuota)).thenReturn(estadoCuota);
        when(estadoCuotaMapper.toDto(estadoCuota)).thenReturn(estadoCuotaResponse);

        EstadoCuotaResponse result = estadoCuotaService.saveEstadoCuota(estadoCuotaRequest);

        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("PENDIENTE", result.nombre())
        );
        verify(estadoCuotaRepository).save(estadoCuota);
    }
    @Test
    @DisplayName("prueba de actualizar estado de cuota")
    void updateEstadoCuotaTest() {
        Long estadoCuotaId = 1L;
        EstadoCuotaRequest estadoCuotaRequest = EstadoCuotaRequest.builder()
                .nombre("PENDIENTE")
                .build();
        EstadoCuota estadoCuota = EstadoCuota.builder().id(estadoCuotaId).nombre("PENDIENTE").build();
        EstadoCuotaResponse estadoCuotaResponse = EstadoCuotaResponse.builder().id(estadoCuotaId).nombre("PENDIENTE").build();

        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.of(estadoCuota));
        when(estadoCuotaRepository.save(estadoCuota)).thenReturn(estadoCuota);
        when(estadoCuotaMapper.toDto(estadoCuota)).thenReturn(estadoCuotaResponse);
        EstadoCuotaResponse result = estadoCuotaService.updateEstadoCuota(estadoCuotaId, estadoCuotaRequest);
        assertAll(
                () -> assertEquals(estadoCuotaId, result.id()),
                () -> assertEquals("PENDIENTE", result.nombre())
        );
        verify(estadoCuotaRepository).findById(estadoCuotaId);
        verify(estadoCuotaRepository).save(estadoCuota);
    }
    @Test
    @DisplayName("prueba de actualizar estado de cuota ResourceNotFound")
    void updateEstadoCuotaResourceNotFoundTest() {
        Long estadoCuotaId = 1L;
        EstadoCuotaRequest estadoCuotaRequest = EstadoCuotaRequest.builder()
                .nombre("PENDIENTE")
                .build();
        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> estadoCuotaService.updateEstadoCuota(estadoCuotaId, estadoCuotaRequest));
        assertEquals("Estado de cuota no encontrado con id: " + estadoCuotaId, exceoption.getMessage());
        verify(estadoCuotaRepository).findById(estadoCuotaId);
    }
    @Test
    @DisplayName("prueba de eliminar estado de cuota")
    void deleteEstadoCuotaTest() {
        Long estadoCuotaId = 1L;
        EstadoCuota estadoCuota = EstadoCuota.builder().id(estadoCuotaId).nombre("PENDIENTE").build();
        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.of(estadoCuota));

        estadoCuotaService.deleteEstadoCuota(estadoCuotaId);

        verify(estadoCuotaRepository).findById(estadoCuotaId);
        verify(estadoCuotaRepository).delete(estadoCuota);
    }
    @Test
    @DisplayName("prueba de eliminar estado de cuota ResourceNotFound")
    void deleteEstadoCuotaResourceNotFoundTest() {
        Long estadoCuotaId = 1L;
        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> estadoCuotaService.deleteEstadoCuota(estadoCuotaId));
        assertEquals("Estado de cuota no encontrado con id: " + estadoCuotaId, exceoption.getMessage());
        verify(estadoCuotaRepository).findById(estadoCuotaId);
    }
    @Test
    @DisplayName("Prueba de obtener estado de cuota entidad por ID")
    void getEstadoCuotaEntityByIdTest() {
        Long estadoCuotaId = 1L;
        EstadoCuota estadoCuota = EstadoCuota.builder().id(estadoCuotaId).nombre("PENDIENTE").build();

        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(java.util.Optional.of(estadoCuota));
        EstadoCuota result = estadoCuotaService.getEstadoCuotaEntityById(estadoCuotaId);
        assertAll(
                () -> assertEquals(estadoCuotaId, result.getId()),
                () -> assertEquals("PENDIENTE", result.getNombre())
        );
        verify(estadoCuotaRepository).findById(estadoCuotaId);
    }
    @Test
    @DisplayName("Prueba de obtener estado de cuota entidad ResourceNotFound")
    void getEstadoCuotaEntityByIdResourceNotFoundTest() {
        Long estadoCuotaId = 1L;
        when(estadoCuotaRepository.findById(estadoCuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> estadoCuotaService.getEstadoCuotaEntityById(estadoCuotaId));
        assertEquals("Estado de cuota no encontrado con id: " + estadoCuotaId, exceoption.getMessage());
        verify(estadoCuotaRepository).findById(estadoCuotaId);
    }
}

