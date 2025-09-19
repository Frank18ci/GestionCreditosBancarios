package org.nttdata.com.serviciotransacciones.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionResponse;
import org.nttdata.com.serviciotransacciones.model.TipoTransaccion;
import org.nttdata.com.serviciotransacciones.repository.TipoTransaccionRepository;
import org.nttdata.com.serviciotransacciones.service.impl.TipoTransaccionServiceImpl;
import org.nttdata.com.serviciotransacciones.util.TipoTransaccionMapper;

import java.lang.module.ResolutionException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoTransaccionServiceImplTest {

    private TipoTransaccionRepository tipoTransaccionRepository;
    private TipoTransaccionMapper tipoTransaccionMapper;
    private TipoTransaccionServiceImpl tipoTransaccionService;

    @BeforeEach
    void setUp() {
        tipoTransaccionRepository = mock(TipoTransaccionRepository.class);
        tipoTransaccionMapper = mock(TipoTransaccionMapper.class);
        tipoTransaccionService = new TipoTransaccionServiceImpl(tipoTransaccionRepository, tipoTransaccionMapper);
    }

    @Test
    void getAllTipoTransacciones_WhenNoDataExists_ShouldReturnEmptyList() {
        when(tipoTransaccionRepository.findAll()).thenReturn(Collections.emptyList());
        when(tipoTransaccionMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<TipoTransaccionResponse> result = tipoTransaccionService.getAllTipoTransacciones();

        assertTrue(result.isEmpty());
        verify(tipoTransaccionRepository, times(1)).findAll();
        verify(tipoTransaccionMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    void getTipoTransaccionById_WhenIdIsValid_ShouldReturnTipoTransaccionResponse() {
        Long id = 1L;
        TipoTransaccion tipoTransaccion = new TipoTransaccion();
        TipoTransaccionResponse response = new TipoTransaccionResponse(1L, "Transferencia");

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.of(tipoTransaccion));
        when(tipoTransaccionMapper.toDto(tipoTransaccion)).thenReturn(response);

        TipoTransaccionResponse result = tipoTransaccionService.getTipoTransaccionById(id);

        assertNotNull(result);
        verify(tipoTransaccionRepository, times(1)).findById(id);
        verify(tipoTransaccionMapper, times(1)).toDto(tipoTransaccion);
    }

    @Test
    void getTipoTransaccionById_WhenIdIsInvalid_ShouldThrowResolutionException() {
        Long id = 1L;

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResolutionException.class, () -> tipoTransaccionService.getTipoTransaccionById(id));
        verify(tipoTransaccionRepository, times(1)).findById(id);
        verifyNoInteractions(tipoTransaccionMapper);
    }

    @Test
    void createTipoTransaccion_WhenRequestIsValid_ShouldReturnTipoTransaccionResponse() {
        TipoTransaccionRequest request = new TipoTransaccionRequest("Transferencia");
        TipoTransaccion tipoTransaccion = new TipoTransaccion();
        TipoTransaccionResponse response = new TipoTransaccionResponse(1L, "Transferencia");

        when(tipoTransaccionMapper.toEntity(request)).thenReturn(tipoTransaccion);
        when(tipoTransaccionRepository.save(tipoTransaccion)).thenReturn(tipoTransaccion);
        when(tipoTransaccionMapper.toDto(tipoTransaccion)).thenReturn(response);

        TipoTransaccionResponse result = tipoTransaccionService.createTipoTransaccion(request);

        assertNotNull(result);
        verify(tipoTransaccionMapper, times(1)).toEntity(request);
        verify(tipoTransaccionRepository, times(1)).save(tipoTransaccion);
        verify(tipoTransaccionMapper, times(1)).toDto(tipoTransaccion);
    }

    @Test
    void updateTipoTransaccion_WhenIdAndRequestAreValid_ShouldReturnUpdatedTipoTransaccionResponse() {
        Long id = 1L;
        TipoTransaccionRequest request = new TipoTransaccionRequest("Pago");
        TipoTransaccion tipoTransaccion = new TipoTransaccion();
        TipoTransaccionResponse response = new TipoTransaccionResponse(1L, "Pago");

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.of(tipoTransaccion));
        when(tipoTransaccionRepository.save(tipoTransaccion)).thenReturn(tipoTransaccion);
        when(tipoTransaccionMapper.toDto(tipoTransaccion)).thenReturn(response);

        TipoTransaccionResponse result = tipoTransaccionService.updateTipoTransaccion(id, request);

        assertNotNull(result);
        verify(tipoTransaccionRepository, times(1)).findById(id);
        verify(tipoTransaccionRepository, times(1)).save(tipoTransaccion);
        verify(tipoTransaccionMapper, times(1)).toDto(tipoTransaccion);
    }

    @Test
    void updateTipoTransaccion_WhenIdIsInvalid_ShouldThrowResolutionException() {
        Long id = 1L;
        TipoTransaccionRequest request = new TipoTransaccionRequest("Pago");

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResolutionException.class, () -> tipoTransaccionService.updateTipoTransaccion(id, request));
        verify(tipoTransaccionRepository, times(1)).findById(id);
        verifyNoInteractions(tipoTransaccionMapper);
    }

    @Test
    void deleteTipoTransaccion_WhenIdIsValid_ShouldDeleteTipoTransaccion() {
        Long id = 1L;
        TipoTransaccion tipoTransaccion = new TipoTransaccion();

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.of(tipoTransaccion));
        doNothing().when(tipoTransaccionRepository).delete(tipoTransaccion);

        tipoTransaccionService.deleteTipoTransaccion(id);

        verify(tipoTransaccionRepository, times(1)).findById(id);
        verify(tipoTransaccionRepository, times(1)).delete(tipoTransaccion);
    }

    @Test
    void deleteTipoTransaccion_WhenIdIsInvalid_ShouldThrowResolutionException() {
        Long id = 1L;

        when(tipoTransaccionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResolutionException.class, () -> tipoTransaccionService.deleteTipoTransaccion(id));
        verify(tipoTransaccionRepository, times(1)).findById(id);
        verify(tipoTransaccionRepository, times(0)).delete(any());
    }
}
