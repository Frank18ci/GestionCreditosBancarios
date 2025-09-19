package org.nttdata.com.serviciocuentas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nttdata.com.serviciocuentas.dto.CuentaRequest;
import org.nttdata.com.serviciocuentas.dto.CuentaResponse;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.Cuenta;
import org.nttdata.com.serviciocuentas.repository.CuentaRepository;
import org.nttdata.com.serviciocuentas.util.CuentaMapper;
import org.nttdata.com.serviciocuentas.service.impl.CuentaServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaServiceImplTest {

    private CuentaRepository cuentaRepository;
    private CuentaMapper cuentaMapper;
    private EstadoCuentaService estadoCuentaService;
    private CuentaServiceImpl cuentaService;

    @BeforeEach
    void setUp() {
        cuentaRepository = mock(CuentaRepository.class);
        cuentaMapper = mock(CuentaMapper.class);
        estadoCuentaService = mock(EstadoCuentaService.class);
        cuentaService = new CuentaServiceImpl(cuentaRepository, cuentaMapper, estadoCuentaService);
    }

    @Test
    void crearCuenta_WhenRequestIsValid_ShouldReturnCuentaResponse() {
        CuentaRequest request = new CuentaRequest(1L, 1L, 1L, BigDecimal.TEN);
        Cuenta cuenta = new Cuenta();
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(1L, 1L, tipoCuenta, estadoCuenta, BigDecimal.TEN);

        when(cuentaMapper.toEntity(request)).thenReturn(cuenta);
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toDto(cuenta)).thenReturn(response);

        CuentaResponse result = cuentaService.crearCuenta(request);

        assertNotNull(result);
        verify(cuentaMapper, times(1)).toEntity(request);
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(cuentaMapper, times(1)).toDto(cuenta);
    }

    @Test
    void obtenerCuentaPorId_WhenIdIsValid_ShouldReturnCuentaResponse() {
        Long id = 1L;
        Cuenta cuenta = new Cuenta();
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(id, 1L, tipoCuenta, estadoCuenta, BigDecimal.TEN);

        when(cuentaRepository.findById(id)).thenReturn(Optional.of(cuenta));
        when(cuentaMapper.toDto(cuenta)).thenReturn(response);

        CuentaResponse result = cuentaService.obtenerCuentaPorId(id);

        assertNotNull(result);
        verify(cuentaRepository, times(1)).findById(id);
        verify(cuentaMapper, times(1)).toDto(cuenta);
    }

    @Test
    void obtenerCuentaPorId_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(cuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> cuentaService.obtenerCuentaPorId(id));
        verify(cuentaRepository, times(1)).findById(id);
        verifyNoInteractions(cuentaMapper);
    }

    @Test
    void actualizarSaldo_WhenIdIsValid_ShouldUpdateSaldo() {
        Long id = 1L;
        BigDecimal nuevoSaldo = BigDecimal.valueOf(100);
        Cuenta cuenta = new Cuenta();
        TipoCuentaResponse tipoCuenta = new TipoCuentaResponse(1L, "Ahorro");
        EstadoCuentaResponse estadoCuenta = new EstadoCuentaResponse(1L, "Activa");
        CuentaResponse response = new CuentaResponse(id, 1L, tipoCuenta, estadoCuenta, nuevoSaldo);

        when(cuentaRepository.findById(id)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);
        when(cuentaMapper.toDto(cuenta)).thenReturn(response);

        CuentaResponse result = cuentaService.actualizarSaldo(id, nuevoSaldo);

        assertNotNull(result);
        verify(cuentaRepository, times(1)).findById(id);
        verify(cuentaRepository, times(1)).save(cuenta);
        verify(cuentaMapper, times(1)).toDto(cuenta);
    }

    @Test
    void actualizarSaldo_WhenIdIsInvalid_ShouldThrowRuntimeException() {
        Long id = 1L;
        BigDecimal nuevoSaldo = BigDecimal.valueOf(100);

        when(cuentaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cuentaService.actualizarSaldo(id, nuevoSaldo));
        verify(cuentaRepository, times(1)).findById(id);
        verify(cuentaRepository, times(0)).save(any());
    }

    @Test
    void eliminarCuenta_WhenIdIsValid_ShouldDeleteCuenta() {
        Long id = 1L;

        when(cuentaRepository.existsById(id)).thenReturn(true);
        doNothing().when(cuentaRepository).deleteById(id);

        cuentaService.eliminarCuenta(id);

        verify(cuentaRepository, times(1)).existsById(id);
        verify(cuentaRepository, times(1)).deleteById(id);
    }

    @Test
    void eliminarCuenta_WhenIdIsInvalid_ShouldThrowResourceNotFound() {
        Long id = 1L;

        when(cuentaRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> cuentaService.eliminarCuenta(id));
        verify(cuentaRepository, times(1)).existsById(id);
        verify(cuentaRepository, times(0)).deleteById(any());
    }
}
