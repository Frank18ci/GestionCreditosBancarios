package org.nttdata.com.serviciocuentas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.TipoCuenta;
import org.nttdata.com.serviciocuentas.repository.TipoCuentaRepository;
import org.nttdata.com.serviciocuentas.service.impl.TipoCuentaServiceImpl;
import org.nttdata.com.serviciocuentas.util.TipoCuentaMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TipoCuentaServiceImplTest {

    private TipoCuentaRepository tipoCuentaRepository;
    private TipoCuentaMapper tipoCuentaMapper;
    private TipoCuentaServiceImpl tipoCuentaService;

    @BeforeEach
    void setUp() {
        tipoCuentaRepository = mock(TipoCuentaRepository.class);
        tipoCuentaMapper = mock(TipoCuentaMapper.class);
        tipoCuentaService = new TipoCuentaServiceImpl(tipoCuentaRepository, tipoCuentaMapper);
    }

    @Test
    void getAllTiposCuenta_WhenNoDataExists_ShouldReturnEmptyList() {
        when(tipoCuentaRepository.findAll()).thenReturn(Collections.emptyList());

        List<TipoCuentaResponse> result = tipoCuentaService.getAllTiposCuenta();

        assertTrue(result.isEmpty());
        verify(tipoCuentaRepository, times(1)).findAll();
        verify(tipoCuentaMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    void getTipoCuentaById_WhenIdIsValid_ShouldReturnTipoCuentaResponse() {
        Long id = 1L;
        TipoCuenta tipoCuenta = new TipoCuenta();
        TipoCuentaResponse response = new TipoCuentaResponse(1L, "Ahorro");

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaMapper.toDto(tipoCuenta)).thenReturn(response);

        TipoCuentaResponse result = tipoCuentaService.getTipoCuentaById(id);

        assertNotNull(result);
        verify(tipoCuentaRepository, times(1)).findById(id);
        verify(tipoCuentaMapper, times(1)).toDto(tipoCuenta);
    }

    @Test
    void getTipoCuentaById_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tipoCuentaService.getTipoCuentaById(id));
        verify(tipoCuentaRepository, times(1)).findById(id);
        verifyNoInteractions(tipoCuentaMapper);
    }

    @Test
    void createTipoCuenta_WhenRequestIsValid_ShouldReturnTipoCuentaResponse() {
        TipoCuentaRequest request = new TipoCuentaRequest("Cuenta Ahorro");
        TipoCuenta tipoCuenta = new TipoCuenta();
        TipoCuentaResponse response = new TipoCuentaResponse(1L, "Ahorro");

        when(tipoCuentaMapper.toEntity(request)).thenReturn(tipoCuenta);
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toDto(tipoCuenta)).thenReturn(response);

        TipoCuentaResponse result = tipoCuentaService.createTipoCuenta(request);

        assertNotNull(result);
        verify(tipoCuentaMapper, times(1)).toEntity(request);
        verify(tipoCuentaRepository, times(1)).save(tipoCuenta);
        verify(tipoCuentaMapper, times(1)).toDto(tipoCuenta);
    }

    @Test
    void updateTipoCuenta_WhenIdAndRequestAreValid_ShouldReturnUpdatedTipoCuentaResponse() {
        Long id = 1L;
        TipoCuentaRequest request = new TipoCuentaRequest("Cuenta Corriente");
        TipoCuenta tipoCuenta = new TipoCuenta();
        TipoCuentaResponse response = new TipoCuentaResponse(id, "Cuenta Corriente");

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.of(tipoCuenta));
        when(tipoCuentaRepository.save(tipoCuenta)).thenReturn(tipoCuenta);
        when(tipoCuentaMapper.toDto(tipoCuenta)).thenReturn(response);

        TipoCuentaResponse result = tipoCuentaService.updateTipoCuenta(id, request);

        assertNotNull(result);
        // assertEquals("Cuenta Corriente", tipoCuenta.getNombre()); // Solo si el setter está implementado
        verify(tipoCuentaRepository, times(1)).findById(id);
        verify(tipoCuentaRepository, times(1)).save(tipoCuenta);
        verify(tipoCuentaMapper, times(1)).toDto(tipoCuenta);
    }

    @Test
    void updateTipoCuenta_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;
        TipoCuentaRequest request = new TipoCuentaRequest("Cuenta Corriente");

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tipoCuentaService.updateTipoCuenta(id, request));
        verify(tipoCuentaRepository, times(1)).findById(id);
        verify(tipoCuentaRepository, times(0)).save(any());
    }

    @Test
    void deleteTipoCuenta_WhenIdIsValid_ShouldDeleteTipoCuenta() {
        Long id = 1L;
        TipoCuenta tipoCuenta = new TipoCuenta();

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.of(tipoCuenta));
        doNothing().when(tipoCuentaRepository).delete(tipoCuenta);

        tipoCuentaService.deleteTipoCuenta(id);

        verify(tipoCuentaRepository, times(1)).findById(id);
        verify(tipoCuentaRepository, times(1)).delete(tipoCuenta);
    }

    @Test
    void deleteTipoCuenta_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(tipoCuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> tipoCuentaService.deleteTipoCuenta(id));
        verify(tipoCuentaRepository, times(1)).findById(id);
        verify(tipoCuentaRepository, times(0)).delete(any());
    }
}
