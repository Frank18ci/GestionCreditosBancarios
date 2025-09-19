package org.nttdata.com.serviciocuentas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.EstadoCuenta;
import org.nttdata.com.serviciocuentas.repository.EstadoCuentaRepository;
import org.nttdata.com.serviciocuentas.util.EstadoCuentaMapper;
import org.nttdata.com.serviciocuentas.service.impl.EstadoCuentaServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstadoCuentaServiceImplTest {

    private EstadoCuentaRepository estadoCuentaRepository;
    private EstadoCuentaMapper estadoCuentaMapper;
    private EstadoCuentaServiceImpl estadoCuentaService;

    @BeforeEach
    void setUp() {
        estadoCuentaRepository = mock(EstadoCuentaRepository.class);
        estadoCuentaMapper = mock(EstadoCuentaMapper.class);
        estadoCuentaService = new EstadoCuentaServiceImpl(estadoCuentaRepository, estadoCuentaMapper);
    }

    @Test
    void getAllEstadosCuenta_WhenNoDataExists_ShouldReturnEmptyList() {
        when(estadoCuentaRepository.findAll()).thenReturn(Collections.emptyList());
        when(estadoCuentaMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<EstadoCuentaResponse> result = estadoCuentaService.getAllEstadosCuenta();

        assertTrue(result.isEmpty());
        verify(estadoCuentaRepository, times(1)).findAll();
        verify(estadoCuentaMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    void getEstadoCuentaById_WhenIdIsValid_ShouldReturnEstadoCuentaResponse() {
        Long id = 1L;
        EstadoCuenta estadoCuenta = new EstadoCuenta();
        EstadoCuentaResponse response = new EstadoCuentaResponse(1L, "Activo");

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.of(estadoCuenta));
        when(estadoCuentaMapper
                .toDto(estadoCuenta)).thenReturn(response);

        EstadoCuentaResponse result = estadoCuentaService.getEstadoCuentaById(id);

        assertNotNull(result);
        verify(estadoCuentaRepository, times(1)).findById(id);
        verify(estadoCuentaMapper, times(1)).toDto(estadoCuenta);
    }

    @Test
    void getEstadoCuentaById_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> estadoCuentaService.getEstadoCuentaById(id));
        verify(estadoCuentaRepository, times(1)).findById(id);
        verifyNoInteractions(estadoCuentaMapper);
    }

    @Test
    void saveEstadoCuenta_WhenRequestIsValid_ShouldReturnEstadoCuentaResponse() {
        EstadoCuentaRequest request = new EstadoCuentaRequest("Activo");
        EstadoCuenta estadoCuenta = new EstadoCuenta();
        EstadoCuentaResponse response = new EstadoCuentaResponse(1L, "Activo");

        when(estadoCuentaMapper.toEntity(request)).thenReturn(estadoCuenta);
        when(estadoCuentaRepository.save(estadoCuenta)).thenReturn(estadoCuenta);
        when(estadoCuentaMapper.toDto(estadoCuenta)).thenReturn(response);

        EstadoCuentaResponse result = estadoCuentaService.saveEstadoCuenta(request);

        assertNotNull(result);
        verify(estadoCuentaMapper, times(1)).toEntity(request);
        verify(estadoCuentaRepository, times(1)).save(estadoCuenta);
        verify(estadoCuentaMapper, times(1)).toDto(estadoCuenta);
    }

    @Test
    void updateEstadoCuenta_WhenIdAndRequestAreValid_ShouldReturnUpdatedEstadoCuentaResponse() {
        Long id = 1L;
        EstadoCuentaRequest request = new EstadoCuentaRequest("Updated Name");
        EstadoCuenta estadoCuenta = new EstadoCuenta();
        EstadoCuentaResponse response = new EstadoCuentaResponse(id, "Updated Name");

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.of(estadoCuenta));
        when(estadoCuentaRepository.save(estadoCuenta)).thenReturn(estadoCuenta);
        when(estadoCuentaMapper.toDto(estadoCuenta)).thenReturn(response);

        EstadoCuentaResponse result = estadoCuentaService.updateEstadoCuenta(id, request);

        assertNotNull(result);
        verify(estadoCuentaRepository, times(1)).findById(id);
        verify(estadoCuentaRepository, times(1)).save(estadoCuenta);
        verify(estadoCuentaMapper, times(1)).toDto(estadoCuenta);
    }

    @Test
    void updateEstadoCuenta_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;
        EstadoCuentaRequest request = new EstadoCuentaRequest("Updated Name");

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> estadoCuentaService.updateEstadoCuenta(id, request));
        verify(estadoCuentaRepository, times(1)).findById(id);
        verify(estadoCuentaRepository, times(0)).save(any());
    }

    @Test
    void deleteEstadoCuenta_WhenIdIsValid_ShouldDeleteEstadoCuenta() {
        Long id = 1L;
        EstadoCuenta estadoCuenta = new EstadoCuenta();

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.of(estadoCuenta));
        doNothing().when(estadoCuentaRepository).delete(estadoCuenta);

        estadoCuentaService.deleteEstadoCuenta(id);

        verify(estadoCuentaRepository, times(1)).findById(id);
        verify(estadoCuentaRepository, times(1)).delete(estadoCuenta);
    }

    @Test
    void deleteEstadoCuenta_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(estadoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> estadoCuentaService.deleteEstadoCuenta(id));
        verify(estadoCuentaRepository, times(1)).findById(id);
        verify(estadoCuentaRepository, times(0)).delete(any());
    }
}
