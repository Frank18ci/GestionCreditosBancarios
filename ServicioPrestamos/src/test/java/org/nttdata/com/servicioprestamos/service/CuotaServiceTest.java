package org.nttdata.com.servicioprestamos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.client.CuentaClient;
import org.nttdata.com.servicioprestamos.client.TransaccionClient;
import org.nttdata.com.servicioprestamos.client.dto.CuentaResponse;
import org.nttdata.com.servicioprestamos.client.dto.EstadoCuentaResponse;
import org.nttdata.com.servicioprestamos.client.dto.TipoCuentaResponse;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.dto.*;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.Cuota;
import org.nttdata.com.servicioprestamos.models.EstadoCuota;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.nttdata.com.servicioprestamos.repository.CuotaRepository;
import org.nttdata.com.servicioprestamos.util.CuotaMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CuotaServiceTest {
    @InjectMocks
    private CuotaServiceImpl cuotaService;
    @Mock
    private CuotaMapper cuotaMapper;
    @Mock
    private CuentaClient cuentaClient;
    @Mock
    private TransaccionClient transaccionClient;
    @Mock
    private CuotaRepository cuotaRepository;
    @Test
    @DisplayName("Prueba de listar cuotas")
    void getAllCuotasTest() {
        List<CuotaResponse> cuotasResponse = List.of(
                CuotaResponse.builder()
                        .id(1L)
                        .numero(1)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(EstadoCuotaResponse.builder()
                                .id(1L)
                                .nombre("PENDIENTE")
                                .build())
                        .prestamo(PrestamoResponse.builder()
                                .id(1L)
                                .monto(new BigDecimal("1000.00"))
                                .plazoMeses(12)
                                .tasaInteres(new BigDecimal("5.00"))
                                .fechaDesembolso(new Date())
                                .estadoPrestamo(EstadoPrestamoResponse.builder()
                                        .id(1L)
                                        .nombre("ACTIVO")
                                        .build())
                                .build())
                        .build(),
                CuotaResponse.builder()
                        .id(2L)
                        .numero(2)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(EstadoCuotaResponse.builder()
                                .id(2L)
                                .nombre("PAGADO")
                                .build())
                        .prestamo(PrestamoResponse.builder()
                                .id(1L)
                                .monto(new BigDecimal("1000.00"))
                                .plazoMeses(12)
                                .tasaInteres(new BigDecimal("5.00"))
                                .fechaDesembolso(new Date())
                                .estadoPrestamo(EstadoPrestamoResponse.builder()
                                        .id(1L)
                                        .nombre("ACTIVO")
                                        .build())
                                .build())
                        .build()
        );
        List<Cuota> cuotas = List.of(
                Cuota.builder()
                        .id(1L)
                        .numero(1)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(null)
                        .prestamo(null)
                        .build(),
                Cuota.builder()
                        .id(2L)
                        .numero(2)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(null)
                        .prestamo(null)
                        .build()
        );
        when(cuotaRepository.findAll()).thenReturn(cuotas);
        when(cuotaMapper.toDtoList(cuotas)).thenReturn(cuotasResponse);
        List<CuotaResponse> result = cuotaService.getAllCuotas();
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.getFirst().id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(cuotaRepository).findAll();
        verify(cuotaMapper).toDtoList(cuotas);
    }
    @Test
    @DisplayName("Prueba de obtener cuota por ID")
    void getCuotaByIdTest() {
        Long cuotaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                        .build())
                .build())
                .build();
        CuotaResponse cuotaResponse = CuotaResponse.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuotaResponse.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(PrestamoResponse.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamoResponse.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaResponse);
        CuotaResponse result = cuotaService.getCuotaById(cuotaId);
        assertAll(
                () -> assertEquals(cuotaId, result.id()),
                () -> assertEquals(1, result.numero()),
                () -> assertEquals(new BigDecimal("100.00"), result.monto()),
                () -> assertEquals(1L, result.estadoCuota().id()),
                () -> assertEquals(1L, result.prestamo().id())
        );
        verify(cuotaRepository).findById(cuotaId);
        verify(cuotaMapper).toDto(cuota);
    }
    @Test
    @DisplayName("Prueba de obtener cuota ResourceNotFound")
    void getCuotaByIdResourceNotFoundTest() {
        Long cuotaId = 1L;
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> cuotaService.getCuotaById(cuotaId));
        assertEquals("Cuota no encontrada con id: " + cuotaId, exceoption.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de crear cuota")
    void saveCuotaTest() {
        CuotaRequest cuotaRequest = CuotaRequest.builder()
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuotaId(1L)
                .prestamoId(1L)
                .build();
        Cuota cuota = Cuota.builder()
                .id(1L)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        CuotaResponse cuotaResponse = CuotaResponse.builder()
                .id(1L)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuotaResponse.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(PrestamoResponse.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamoResponse.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaMapper.toEntity(cuotaRequest)).thenReturn(cuota);
        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaResponse);
        CuotaResponse result = cuotaService.saveCuota(cuotaRequest);
        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals(1, result.numero()),
                () -> assertEquals(new BigDecimal("100.00"), result.monto()),
                () -> assertEquals(1L, result.estadoCuota().id()),
                () -> assertEquals(1L, result.prestamo().id())
        );
        verify(cuotaMapper).toEntity(cuotaRequest);
        verify(cuotaRepository).save(cuota);
        verify(cuotaMapper).toDto(cuota);
    }
    @Test
    @DisplayName("prueba de actualizar cuota")
    void updateCuotaTest() {
        Long cuotaId = 1L;
        CuotaRequest cuotaRequest = CuotaRequest.builder()
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuotaId(1L)
                .prestamoId(1L)
                .build();
        Cuota cuotaEntityRequest = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        Cuota cuotaFound = Cuota.builder()
                .id(cuotaId)
                .numero(2)
                .monto(new BigDecimal("200.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(2L)
                        .nombre("PAGADO")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(2L)
                        .monto(new BigDecimal("2000.00"))
                        .plazoMeses(24)
                        .tasaInteres(new BigDecimal("10.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(2L)
                                .nombre("INACTIVO")
                                .build())
                        .build())
                .build();
        CuotaResponse cuotaResponse = CuotaResponse.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuotaResponse.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(PrestamoResponse.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamoResponse.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaMapper.toEntity(cuotaRequest)).thenReturn(cuotaEntityRequest);
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuotaFound));
        when(cuotaRepository.save(cuotaFound)).thenReturn(cuotaFound);
        when(cuotaMapper.toDto(cuotaFound)).thenReturn(cuotaResponse);
        CuotaResponse result = cuotaService.updateCuota(cuotaId, cuotaRequest);
        assertAll(
                () -> assertEquals(cuotaId, result.id()),
                () -> assertEquals(1, result.numero()),
                () -> assertEquals(new BigDecimal("100.00"), result.monto()),
                () -> assertEquals(1L, result.estadoCuota().id()),
                () -> assertEquals(1L, result.prestamo().id())
        );
        verify(cuotaMapper).toEntity(cuotaRequest);
        verify(cuotaRepository).findById(cuotaId);
        verify(cuotaRepository).save(cuotaFound);
        verify(cuotaMapper).toDto(cuotaFound);
    }
    @Test
    @DisplayName("prueba de actualizar cuota ResourceNotFound")
    void updateCuotaResourceNotFoundTest() {
        Long cuotaId = 1L;
        CuotaRequest cuotaRequest = CuotaRequest.builder()
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuotaId(1L)
                .prestamoId(1L)
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> cuotaService.updateCuota(cuotaId, cuotaRequest));
        assertEquals("Cuota no encontrada con id: " + cuotaId, exceoption.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de eliminar cuota")
    void deleteCuotaTest() {
        Long cuotaId = 1L;
        Cuota cuotaFound = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuotaFound));
        cuotaService.deleteCuota(cuotaId);
        verify(cuotaRepository).findById(cuotaId);
        verify(cuotaRepository).delete(cuotaFound);
    }
    @Test
    @DisplayName("prueba de eliminar cuota ResourceNotFound")
    void deleteCuotaResourceNotFoundTest() {
        Long cuotaId = 1L;
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exceoption = assertThrows(ResourceNotFound.class, () -> cuotaService.deleteCuota(cuotaId));
        assertEquals("Cuota no encontrada con id: " + cuotaId, exceoption.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota exitosamente")
    void pagarCuotaTestSuccess() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        CuotaResponse cuotaResponse = CuotaResponse.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuotaResponse.builder()
                        .id(2L)
                        .nombre("PAGADO")
                        .build())
                .prestamo(PrestamoResponse.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamoResponse.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .id(cuentaId)
                .tipoCuenta(TipoCuentaResponse.builder()
                        .id(1L)
                        .nombre("AHORRO")
                        .build())
                .estadoCuenta(EstadoCuentaResponse.builder()
                        .id(1L)
                        .nombre("ACTIVA")
                        .build())
                .clienteId(1L)
                .saldo(new BigDecimal("1000.00"))
                .build();

        when(transaccionClient.crearTransaccion(any(TransaccionRequest.class))).thenReturn(null);
        when(cuentaClient.getCuentaById(cuentaId)).thenReturn(cuentaResponse);
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));

        when(cuotaRepository.save(cuota)).thenReturn(cuota);
        when(cuotaMapper.toDto(cuota)).thenReturn(cuotaResponse);
        CuotaResponse result = cuotaService.pagarCuota(cuotaId, cuentaId);
        assertAll(
                () -> assertEquals(cuotaId, result.id()),
                () -> assertEquals(1, result.numero()),
                () -> assertEquals(new BigDecimal("100.00"), result.monto()),
                () -> assertEquals(2L, result.estadoCuota().id()),
                () -> assertEquals(1L, result.prestamo().id())
        );
        verify(cuotaRepository).findById(cuotaId);
        verify(cuotaRepository).save(cuota);
        verify(cuotaMapper).toDto(cuota);
    }
    @Test
    @DisplayName("prueba de pagar cuota con cuota no encontrada")
    void pagarCuotaNotFoundTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertEquals("Cuota no encontrada con id: " + cuotaId, exception.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota con estado no pendiente")
    void pagarCuotaNotPendingTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(2L)
                        .nombre("PAGADO")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertEquals("La cuota no se encuentra en estado PENDIENTE", exception.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota con monto inválido")
    void pagarCuotaInvalidAmountTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("-100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertEquals("Monto de cuota inválido", exception.getMessage());
        verify(cuotaRepository).findById(cuotaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota con cuenta no encontrada")
    void pagarCuotaAccountNotFoundTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        when(cuentaClient.getCuentaById(cuentaId)).thenThrow(new RuntimeException("Cuenta no encontrada"));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertTrue(exception.getMessage().contains("No se pudo consultar la cuenta"));
        verify(cuotaRepository).findById(cuotaId);
        verify(cuentaClient).getCuentaById(cuentaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota con saldo insuficiente")
    void pagarCuotaInsufficientBalanceTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .id(cuentaId)
                .clienteId(1L)
                .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHORROS").build())
                .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVA").build())
                .saldo(new BigDecimal("50.00"))
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        when(cuentaClient.getCuentaById(cuentaId)).thenReturn(cuentaResponse);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertEquals("Saldo insuficiente en la cuenta para pagar la cuota", exception.getMessage());
        verify(cuotaRepository).findById(cuotaId);
        verify(cuentaClient).getCuentaById(cuentaId);
    }
    @Test
    @DisplayName("prueba de pagar cuota con error al registrar transacción")
    void pagarCuotaTransactionErrorTest() {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
        Cuota cuota = Cuota.builder()
                .id(cuotaId)
                .numero(1)
                .monto(new BigDecimal("100.00"))
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder()
                        .id(1L)
                        .nombre("PENDIENTE")
                        .build())
                .prestamo(Prestamo.builder()
                        .id(1L)
                        .monto(new BigDecimal("1000.00"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.00"))
                        .fechaDesembolso(new Date())
                        .estadoPrestamo(EstadoPrestamo.builder()
                                .id(1L)
                                .nombre("ACTIVO")
                                .build())
                        .build())
                .build();
        CuentaResponse cuentaResponse = CuentaResponse.builder()
                .id(cuentaId)
                .clienteId(1L)
                .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHORROS").build())
                .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVA").build())
                .saldo(new BigDecimal("1000.00"))
                .build();
        TransaccionRequest transaccion = TransaccionRequest.builder()
                .cuentaId(cuentaId)
                .monto(cuota.getMonto())
                .tipoTransaccionId(3L)
                .referencia("Pago cuota id:" + cuotaId)
                .fecha(new Date())
                .build();
        when(cuotaRepository.findById(cuotaId)).thenReturn(Optional.of(cuota));
        when(cuentaClient.getCuentaById(cuentaId)).thenReturn(cuentaResponse);
        when(transaccionClient.crearTransaccion(transaccion)).thenThrow(new IllegalStateException("Error al registrar transacción en cuenta"));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cuotaService.pagarCuota(cuotaId, cuentaId));
        assertTrue(exception.getMessage().contains("Error al registrar transacción en cuenta"));
        verify(cuotaRepository).findById(cuotaId);
        verify(cuentaClient).getCuentaById(cuentaId);
        verify(transaccionClient).crearTransaccion(any(TransaccionRequest.class));
    }
    @Test
    @DisplayName("prueba de obtener cuotas por préstamo ID")
    void getCuotasByPrestamoId() {
        Long prestamoId = 1L;
        List<CuotaResponse> cuotasResponse = List.of(
                CuotaResponse.builder()
                        .id(1L)
                        .numero(1)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(EstadoCuotaResponse.builder()
                                .id(1L)
                                .nombre("PENDIENTE")
                                .build())
                        .prestamo(PrestamoResponse.builder()
                                .id(prestamoId)
                                .monto(new BigDecimal("1000.00"))
                                .plazoMeses(12)
                                .tasaInteres(new BigDecimal("5.00"))
                                .fechaDesembolso(new Date())
                                .estadoPrestamo(EstadoPrestamoResponse.builder()
                                        .id(1L)
                                        .nombre("ACTIVO")
                                        .build())
                                .build())
                        .build(),
                CuotaResponse.builder()
                        .id(2L)
                        .numero(2)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(EstadoCuotaResponse.builder()
                                .id(2L)
                                .nombre("PAGADO")
                                .build())
                        .prestamo(PrestamoResponse.builder()
                                .id(prestamoId)
                                .monto(new BigDecimal("1000.00"))
                                .plazoMeses(12)
                                .tasaInteres(new BigDecimal("5.00"))
                                .fechaDesembolso(new Date())
                                .estadoPrestamo(EstadoPrestamoResponse.builder()
                                        .id(1L)
                                        .nombre("ACTIVO")
                                        .build())
                                .build())
                        .build()
        );
        List<Cuota> cuotas = List.of(
                Cuota.builder()
                        .id(1L)
                        .numero(1)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(null)
                        .prestamo(null)
                        .build(),
                Cuota.builder()
                        .id(2L)
                        .numero(2)
                        .monto(new BigDecimal("100.00"))
                        .fechaVencimiento(new Date())
                        .estadoCuota(null)
                        .prestamo(null)
                        .build()
        );
        when(cuotaRepository.findByPrestamoId(prestamoId)).thenReturn(cuotas);
        when(cuotaMapper.toDtoList(cuotas)).thenReturn(cuotasResponse);
        List<CuotaResponse> result = cuotaService.getCuotasByPrestamoId(prestamoId);
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.getFirst().id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(cuotaRepository).findByPrestamoId(prestamoId);
        verify(cuotaMapper).toDtoList(cuotas);
    }
}
