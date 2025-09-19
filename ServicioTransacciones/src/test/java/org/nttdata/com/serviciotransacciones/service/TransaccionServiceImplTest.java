package org.nttdata.com.serviciotransacciones.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciotransacciones.client.CuentaClient;
import org.nttdata.com.serviciotransacciones.client.dto.CuentaResponse;
import org.nttdata.com.serviciotransacciones.client.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciotransacciones.client.dto.TipoCuentaResponse;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionResponse;
import org.nttdata.com.serviciotransacciones.dto.TransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TransaccionResponse;
import org.nttdata.com.serviciotransacciones.exception.ResourceNotFound;
import org.nttdata.com.serviciotransacciones.model.Transaccion;
import org.nttdata.com.serviciotransacciones.repository.TransaccionRepository;
import org.nttdata.com.serviciotransacciones.util.TransaccionMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionServiceImplTest {

    private TransaccionRepository transaccionRepository;
    private TransaccionMapper transaccionMapper;
    private CuentaClient cuentaClient;
    private TransaccionServiceImpl transaccionService;

    @BeforeEach
    void setUp() {
        transaccionRepository = mock(TransaccionRepository.class);
        transaccionMapper = mock(TransaccionMapper.class);
        cuentaClient = mock(CuentaClient.class);
        transaccionService = new TransaccionServiceImpl(transaccionRepository, transaccionMapper, cuentaClient);
    }

    @Test
    void getAllTransacciones_WhenNoDataExists_ShouldReturnEmptyList() {
        when(transaccionRepository.findAll()).thenReturn(Collections.emptyList());
        when(transaccionMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<TransaccionResponse> result = transaccionService.getAllTransacciones();

        assertTrue(result.isEmpty());
        verify(transaccionRepository, times(1)).findAll();
        verify(transaccionMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    void getTransaccionById_WhenIdIsValid_ShouldReturnTransaccionResponse() {
        Long id = 1L;
        Transaccion transaccion = new Transaccion();
        TipoTransaccionResponse tipoTransaccionResponse = new TipoTransaccionResponse(1L, "Transferencia");
        TransaccionResponse response = new TransaccionResponse(
                1L, 1L, tipoTransaccionResponse, BigDecimal.TEN, new Date(), "detalle"
        );

        when(transaccionRepository.findById(id)).thenReturn(Optional.of(transaccion));
        when(transaccionMapper.toDto(transaccion)).thenReturn(response);

        TransaccionResponse result = transaccionService.getTransaccionById(id);

        assertNotNull(result);
        verify(transaccionRepository, times(1)).findById(id);
        verify(transaccionMapper, times(1)).toDto(transaccion);
    }

    @Test
    void getTransaccionById_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(transaccionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> transaccionService.getTransaccionById(id));
        verify(transaccionRepository, times(1)).findById(id);
        verifyNoInteractions(transaccionMapper);
    }

    @Test
    void createTransaccion_WhenRequestIsValid_ShouldReturnTransaccionResponse() {
        TransaccionRequest request = new TransaccionRequest(
                1L, 1L, BigDecimal.TEN, new Date(), "detalle"
        );
        Transaccion transaccion = new Transaccion();
        TipoTransaccionResponse tipoTransaccionResponse = new TipoTransaccionResponse(1L, "Transferencia");
        TransaccionResponse response = new TransaccionResponse(
                1L, 1L, tipoTransaccionResponse, BigDecimal.TEN, new Date(), "detalle"
        );

        when(transaccionMapper.toEntity(request)).thenReturn(transaccion);
        when(transaccionRepository.save(transaccion)).thenReturn(transaccion);
        when(transaccionMapper.toDto(transaccion)).thenReturn(response);

        TransaccionResponse result = transaccionService.createTransaccion(request);

        assertNotNull(result);
        verify(transaccionMapper, times(1)).toEntity(request);
        verify(transaccionRepository, times(1)).save(transaccion);
        verify(transaccionMapper, times(1)).toDto(transaccion);
    }

    @Test
    void getTransaccionesByCuentaId_WhenCuentaExists_ShouldReturnTransacciones() {
        Long cuentaId = 1L;
        TipoCuentaResponse tipoCuentaResponse = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuentaResponse = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse cuentaResponse = new CuentaResponse(
                cuentaId, 1L, tipoCuentaResponse, estadoCuentaResponse, BigDecimal.TEN
        );
        List<Transaccion> transacciones = List.of(new Transaccion());
        TipoTransaccionResponse tipoTransaccionResponse = new TipoTransaccionResponse(1L, "Transferencia");
        List<TransaccionResponse> responses = List.of(
                new TransaccionResponse(1L, 1L, tipoTransaccionResponse, BigDecimal.TEN, new Date(), "detalle")
        );

        when(cuentaClient.getCuentaById(cuentaId)).thenReturn(cuentaResponse);
        when(transaccionRepository.findByCuentaId(cuentaId)).thenReturn(transacciones);
        when(transaccionMapper.toDtoList(transacciones)).thenReturn(responses);

        List<TransaccionResponse> result = transaccionService.getTransaccionesByCuentaId(cuentaId);

        assertEquals(responses, result);
        verify(cuentaClient, times(1)).getCuentaById(cuentaId);
        verify(transaccionRepository, times(1)).findByCuentaId(cuentaId);
        verify(transaccionMapper, times(1)).toDtoList(transacciones);
    }

    @Test
    void getTransaccionesByCuentaId_WhenCuentaDoesNotExist_ShouldThrowResourceNotFound() {
        Long cuentaId = 1L;

        when(cuentaClient.getCuentaById(cuentaId)).thenReturn(null);

        assertThrows(ResourceNotFound.class, () -> transaccionService.getTransaccionesByCuentaId(cuentaId));
        verify(cuentaClient, times(1)).getCuentaById(cuentaId);
        verifyNoInteractions(transaccionRepository, transaccionMapper);
    }

    @Test
    void deleteTransaccion_WhenIdIsValid_ShouldDeleteTransaccion() {
        Long id = 1L;
        Transaccion transaccion = new Transaccion();

        when(transaccionRepository.findById(id)).thenReturn(Optional.of(transaccion));
        doNothing().when(transaccionRepository).delete(transaccion);

        transaccionService.deleteTransaccion(id);

        verify(transaccionRepository, times(1)).findById(id);
        verify(transaccionRepository, times(1)).delete(transaccion);
    }

    @Test
    void deleteTransaccion_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(transaccionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> transaccionService.deleteTransaccion(id));
        verify(transaccionRepository, times(1)).findById(id);
        verify(transaccionRepository, times(0)).delete(any());
    }
}
