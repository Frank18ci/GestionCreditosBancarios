package org.nttdata.com.servicioprestamos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.client.ClienteClient;
import org.nttdata.com.servicioprestamos.client.CuentaClient;
import org.nttdata.com.servicioprestamos.client.TransaccionClient;
import org.nttdata.com.servicioprestamos.client.dto.*;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoResponse;
import org.nttdata.com.servicioprestamos.dto.PrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.BadRequest;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.nttdata.com.servicioprestamos.producer.NotificacionProducer;
import org.nttdata.com.servicioprestamos.producer.dto.NotificacionRequestK;
import org.nttdata.com.servicioprestamos.repository.PrestamoRepository;
import org.nttdata.com.servicioprestamos.util.PrestamoMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceImplTest {
    @Mock
    private PrestamoMapper prestamoMapper;
    @Mock
    private PrestamoRepository prestamoRepository;
    @Mock
    private ClienteClient clienteClient;
    @Mock
    private CuentaClient cuentaClient;
    @Mock
    private TransaccionClient transaccionClient;
    @Mock
    private CuotaServiceImpl cuotaService;
    @Mock
    private NotificacionProducer notificacionProducer;

    @InjectMocks
    private PrestamoServiceImpl prestamoService;

    @Test
    @DisplayName("Prueba de listar todos los préstamos")
    void getAllPrestamosTest() {
        List<PrestamoResponse> prestamos = List.of(
                PrestamoResponse.builder()
                        .id(1L)
                        .clienteId(1L)
                        .cuentaId(1L)
                        .monto(new BigDecimal("1000.0"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build(),
                PrestamoResponse.builder()
                        .id(2L)
                        .clienteId(2L)
                        .cuentaId(2L)
                        .monto(new BigDecimal("2000.0"))
                        .plazoMeses(24)
                        .tasaInteres(new BigDecimal("6.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build()
        );
        List<Prestamo> prestamoEntities = List.of(
                Prestamo.builder()
                        .id(1L)
                        .clienteId(1L)
                        .cuentaId(1L)
                        .monto(new BigDecimal("1000.0"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build(),
                Prestamo.builder()
                        .id(2L)
                        .clienteId(2L)
                        .cuentaId(2L)
                        .monto(new BigDecimal("2000.0"))
                        .plazoMeses(24)
                        .tasaInteres(new BigDecimal("6.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build()
        );
        when(prestamoRepository.findAll()).thenReturn(prestamoEntities);
        when(prestamoMapper.toDtoList(prestamoEntities)).thenReturn(prestamos);
        List<PrestamoResponse> result = prestamoService.getAllPrestamos();
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(1L, result.getFirst().id()),
                () -> assertEquals(2L, result.get(1).id())
        );
        verify(prestamoRepository).findAll();
        verify(prestamoMapper).toDtoList(prestamoEntities);
    }
    @Test
    @DisplayName("Prueba de obtener préstamo por ID")
    void getPrestamoByIdTest() {
        Long prestamoId = 1L;
        Prestamo prestamo = Prestamo.builder()
                .id(prestamoId)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.0"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.0"))
                .fechaDesembolso((Date.valueOf(LocalDate.now())))
                .build();
        PrestamoResponse prestamoResponse = PrestamoResponse.builder()
                .id(prestamoId)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.0"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.0"))
                .fechaDesembolso((Date.valueOf(LocalDate.now())))
                .build();
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.of(prestamo));
        when(prestamoMapper.toDto(prestamo)).thenReturn(prestamoResponse);
        PrestamoResponse result = prestamoService.getPrestamoById(prestamoId);
        assertAll(
                () -> assertEquals(prestamoId, result.id()),
                () -> assertEquals(1L, result.clienteId()),
                () -> assertEquals(1L, result.cuentaId())
        );
        verify(prestamoRepository).findById(prestamoId);
        verify(prestamoMapper).toDto(prestamo);
    }
    @Test
    @DisplayName("Prueba de obtener préstamo ResourceNotFound")
    void getPrestamoByIdResourceNotFoundTest() {
        Long prestamoId = 1L;
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> prestamoService.getPrestamoById(prestamoId)
        );
        assertEquals("Préstamo no encontrado con id: " + prestamoId, exception.getMessage());
        verify(prestamoRepository).findById(prestamoId);
    }
    @Test
    @DisplayName("Debe crear préstamo exitosamente cuando todos los datos son válidos")
    void createPrestamoSuccess() {
        PrestamoRequest request = PrestamoRequest.builder()
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("2000"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10"))
                .build();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(1L).nombre("Juan").dni("12345678").email("juan@test.com")
                .estadoCliente(EstadoClienteResponse.builder().estado("ACTIVO").build())
                .build();
        CuentaResponse cuenta = CuentaResponse.builder()
                .id(1L).clienteId(1L).saldo(new BigDecimal("50000"))
                .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHORROS").build())
                .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVA").build())
                .build();

        Prestamo prestamo = Prestamo.builder()
                .id(1L).monto(request.monto()).plazoMeses(request.plazoMeses())
                .tasaInteres(request.tasaInteres()).estadoPrestamo(EstadoPrestamo.builder().id(1L).nombre("PENDIENTE").build())
                .build();

        PrestamoResponse prestamoResponse = PrestamoResponse.builder()
                .id(1L).monto(new BigDecimal("20000")).plazoMeses(12).tasaInteres(new BigDecimal("0.10"))
                .estadoPrestamo(EstadoPrestamoResponse.builder().id(1L).nombre("PENDIENTE").build())
                .build();

        when(clienteClient.getClienteById(1L)).thenReturn(cliente);
        when(cuentaClient.getCuentaById(1L)).thenReturn(cuenta);

        when(prestamoMapper.toEntity(request)).thenReturn(prestamo);
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);
        when(prestamoMapper.toDto(prestamo)).thenReturn(prestamoResponse);

        when(transaccionClient.obteTransacciones(request.cuentaId()))
                .thenReturn(List.of(
                        TransaccionResponse.builder()
                                .monto(new BigDecimal("10000"))
                                .tipoTransaccion(TipoTransaccionResponse.builder().nombre("DEPÓSITO").build())
                                .build()
                ));

        PrestamoResponse result = prestamoService.createPrestamo(request);

        assertEquals(prestamoResponse.id(), result.id());
        assertEquals("PENDIENTE", result.estadoPrestamo().nombre());

        verify(notificacionProducer).enviarNotificacion(any(NotificacionRequestK.class));
        verify(prestamoRepository).save(prestamo);
    }

    @Test
    @DisplayName("Debe lanzar BadRequest si egresos son mayores que ingresos")
    void evaluarCreditoEgresosMayores() {
        when(transaccionClient.obteTransacciones(1L))
                .thenReturn(List.of(
                        TransaccionResponse.builder().monto(new BigDecimal("100")).tipoTransaccion(TipoTransaccionResponse.builder().nombre("DEPÓSITO").build()).build(),
                        TransaccionResponse.builder().monto(new BigDecimal("200")).tipoTransaccion(TipoTransaccionResponse.builder().nombre("RETIRO").build()).build()
                ));

        assertThrows(BadRequest.class, () -> prestamoService.evalularCredito(1L, new BigDecimal("500")));
    }

    @Test
    @DisplayName("Debe lanzar BadRequest si monto solicitado excede la capacidad de pago")
    void evaluarCreditoMontoMayorCapacidad() {
        when(transaccionClient.obteTransacciones(1L))
                .thenReturn(List.of(
                        TransaccionResponse.builder().monto(new BigDecimal("1000")).tipoTransaccion(TipoTransaccionResponse.builder().nombre("DEPÓSITO").build()).build()
                ));

        assertThrows(BadRequest.class, () -> prestamoService.evalularCredito(1L, new BigDecimal("1000")));
    }

    @Test
    @DisplayName("Debe fallar si el monto es menor a 1000")
    void evaluarMontoMenorMinimo() {
        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.evaluarMonto(12, new BigDecimal("500"), new BigDecimal("0.10")));
    }

    @Test
    @DisplayName("Debe fallar si el plazo es menor a 6 meses")
    void evaluarPlazoMenorMinimo() {
        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.evaluarPlazo(3, new BigDecimal("5000"), new BigDecimal("0.10")));
    }

    @Test
    @DisplayName("Debe fallar si la tasa es mayor a 40%")
    void evaluarTasaInteresMayorMaximo() {
        assertThrows(IllegalArgumentException.class,
                () -> prestamoService.evaluarTasaInteres(new BigDecimal("0.50"), new BigDecimal("20000"), 12));
    }

    @Test
    @DisplayName("Debe actualizar préstamo exitosamente cuando todos los datos son válidos")
    void updatePrestamoSuccess() {
        Long prestamoId = 1L;
        PrestamoRequest request = PrestamoRequest.builder()
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("2000"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10"))
                .estadoPrestamoId(1L)
                .build();
        Prestamo prestamo = Prestamo.builder()
                .id(prestamoId).clienteId(1L).cuentaId(1L).monto(new BigDecimal("2000")).plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10")).fechaDesembolso(Date.valueOf(LocalDate.now()))
                .estadoPrestamo(EstadoPrestamo.builder().id(1L).nombre("PENDIENTE").build())
                .build();
        PrestamoResponse prestamoResponse = PrestamoResponse.builder()
                .id(prestamoId).clienteId(1L).cuentaId(1L).monto(new BigDecimal("2000")).plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10")).fechaDesembolso(Date.valueOf(LocalDate.now()))
                .estadoPrestamo(EstadoPrestamoResponse.builder().id(1L).nombre("PENDIENTE").build())
                .build();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(1L).nombre("Juan").dni("12345678").email("juan@test.com")
                .estadoCliente(EstadoClienteResponse.builder().estado("ACTIVO").build())
                .build();
        CuentaResponse cuenta = CuentaResponse.builder()
                .id(1L).clienteId(1L).saldo(new BigDecimal("50000"))
                .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHORROS").build())
                .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVA").build())
                .build();

        when(clienteClient.getClienteById(1L)).thenReturn(cliente);
        when(cuentaClient.getCuentaById(1L)).thenReturn(cuenta);

        when(transaccionClient.obteTransacciones(request.cuentaId()))
                .thenReturn(List.of(
                        TransaccionResponse.builder()
                                .monto(new BigDecimal("10000"))
                                .tipoTransaccion(TipoTransaccionResponse.builder().nombre("DEPÓSITO").build())
                                .build()
                ));
        when(prestamoMapper.toEntity(request)).thenReturn(prestamo);
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.of(prestamo));
        when(prestamoRepository.save(prestamo)).thenReturn(prestamo);
        when(prestamoMapper.toDto(prestamo)).thenReturn(prestamoResponse);
        PrestamoResponse result = prestamoService.updatePrestamo(prestamoId, request);
        assertAll(
                () -> assertEquals(prestamoId, result.id()),
                () -> assertEquals(1L, result.clienteId()),
                () -> assertEquals(1L, result.cuentaId())
        );
        verify(prestamoRepository).findById(prestamoId);
        verify(prestamoRepository).save(prestamo);
        verify(prestamoMapper).toDto(prestamo);
    }
    @Test
    @DisplayName("Prueba de actualizar préstamo ResourceNotFound")
    void updatePrestamoResourceNotFoundTest() {
        Long prestamoId = 1L;
        PrestamoRequest request = PrestamoRequest.builder()
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("2000"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10"))
                .build();
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> prestamoService.updatePrestamo(prestamoId, request)
        );
        assertEquals("Préstamo no encontrado con id: " + prestamoId, exception.getMessage());
        verify(prestamoRepository).findById(prestamoId);
    }
    @Test
    @DisplayName("Prueba de eliminar préstamo exitosamente")
    void deletePrestamoTest() {
        Long prestamoId = 1L;
        Prestamo prestamo = Prestamo.builder().id(prestamoId).build();
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.of(prestamo));
        doNothing().when(prestamoRepository).delete(prestamo);
        prestamoService.deletePrestamo(prestamoId);
        verify(prestamoRepository).findById(prestamoId);
        verify(prestamoRepository).delete(prestamo);
    }
    @Test
    @DisplayName("Prueba de eliminar préstamo ResourceNotFound")
    void deletePrestamoResourceNotFoundTest() {
        Long prestamoId = 1L;
        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> prestamoService.deletePrestamo(prestamoId)
        );
        assertEquals("Préstamo no encontrado con id: " + prestamoId, exception.getMessage());
        verify(prestamoRepository).findById(prestamoId);
    }
    @Test
    @DisplayName("Debe aceptar préstamo exitosamente cuando todo es válido")
    void aceptarPrestamoSuccess() {
        Long prestamoId = 1L;
        Prestamo prestamo = Prestamo.builder()
                .id(prestamoId)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("5000"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("0.10"))
                .estadoPrestamo(EstadoPrestamo.builder().id(1L).nombre("PENDIENTE").build())
                .build();

        ClienteResponse cliente = ClienteResponse.builder()
                .id(1L).nombre("Juan").dni("12345678").email("juan@test.com")
                .estadoCliente(EstadoClienteResponse.builder().estado("ACTIVO").build())
                .build();
        CuentaResponse cuenta = CuentaResponse.builder()
                .id(1L).clienteId(1L).saldo(new BigDecimal("1000"))
                .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHORROS").build())
                .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVA").build())
                .build();
        PrestamoResponse response = PrestamoResponse.builder()
                .id(prestamoId).clienteId(1L).cuentaId(1L).monto(new BigDecimal("5000"))
                .estadoPrestamo(EstadoPrestamoResponse.builder().id(2L).nombre("ACEPTADO").build())
                .build();

        when(prestamoRepository.findById(prestamoId)).thenReturn(Optional.of(prestamo));
        when(clienteClient.getClienteById(1L)).thenReturn(cliente);
        when(cuentaClient.getCuentaById(1L)).thenReturn(cuenta);
        when(prestamoRepository.findByClienteIdAndEstadoPrestamoId(1L, 2L)).thenReturn(List.of());
        when(prestamoRepository.save(any(Prestamo.class))).thenReturn(prestamo);
        when(prestamoMapper.toDto(any(Prestamo.class))).thenReturn(response);

        PrestamoResponse result = prestamoService.aceptarPrestamo(prestamoId);

        assertEquals(prestamoId, result.id());
        assertEquals(1L, result.clienteId());
        assertEquals(1L, result.cuentaId());

        verify(transaccionClient).crearTransaccion(any(TransaccionRequest.class));
        verify(cuotaService, times(prestamo.getPlazoMeses())).saveCuota(any(CuotaRequest.class));
        verify(cuentaClient).updateCuenta(eq(1L), any(CuentaRequest.class));
        verify(notificacionProducer).enviarNotificacion(any(NotificacionRequestK.class));
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound cuando el préstamo no existe")
    void aceptarPrestamoNotFound() {
        when(prestamoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> prestamoService.aceptarPrestamo(99L));

        verify(prestamoRepository).findById(99L);
    }
    @Test
    @DisplayName("Debe lanzar BadRequest si el cliente ya tiene 3 préstamos activos")
    void aceptarPrestamoConMasDeTresPrestamosActivos() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L).clienteId(1L).cuentaId(1L).monto(new BigDecimal("5000"))
                .plazoMeses(12).tasaInteres(new BigDecimal("0.10"))
                .estadoPrestamo(EstadoPrestamo.builder().id(1L).nombre("PENDIENTE").build())
                .build();

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(clienteClient.getClienteById(1L)).thenReturn(mock(ClienteResponse.class));
        when(cuentaClient.getCuentaById(1L)).thenReturn(mock(CuentaResponse.class));
        when(prestamoRepository.findByClienteIdAndEstadoPrestamoId(1L, 2L))
                .thenReturn(List.of(new Prestamo(), new Prestamo(), new Prestamo()));

        assertThrows(BadRequest.class, () -> prestamoService.aceptarPrestamo(1L));
    }
    @Test
    @DisplayName("Debe lanzar BadRequest si el préstamo no está en estado PENDIENTE")
    void aceptarPrestamoNoPendiente() {
        Prestamo prestamo = Prestamo.builder()
                .id(1L).clienteId(1L).cuentaId(1L).monto(new BigDecimal("5000"))
                .plazoMeses(12).tasaInteres(new BigDecimal("0.10"))
                .estadoPrestamo(EstadoPrestamo.builder().id(2L).nombre("ACEPTADO").build())
                .build();

        when(prestamoRepository.findById(1L)).thenReturn(Optional.of(prestamo));
        when(clienteClient.getClienteById(1L)).thenReturn(mock(ClienteResponse.class));
        when(cuentaClient.getCuentaById(1L)).thenReturn(mock(CuentaResponse.class));
        when(prestamoRepository.findByClienteIdAndEstadoPrestamoId(1L, 2L)).thenReturn(List.of());

        assertThrows(BadRequest.class, () -> prestamoService.aceptarPrestamo(1L));
    }
    @Test
    @DisplayName("Prueba de obtener préstamos por ID de cliente")
    void getPrestamosByClienteIdSuccess() {
        Long clienteId = 1L;
        List<Prestamo> prestamoEntities = List.of(
                Prestamo.builder()
                        .id(1L)
                        .clienteId(clienteId)
                        .cuentaId(1L)
                        .monto(new BigDecimal("1000.0"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build(),
                Prestamo.builder()
                        .id(2L)
                        .clienteId(clienteId)
                        .cuentaId(2L)
                        .monto(new BigDecimal("2000.0"))
                        .plazoMeses(24)
                        .tasaInteres(new BigDecimal("6.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build()
        );
        List<PrestamoResponse> prestamos = List.of(
                PrestamoResponse.builder()
                        .id(1L)
                        .clienteId(clienteId)
                        .cuentaId(1L)
                        .monto(new BigDecimal("1000.0"))
                        .plazoMeses(12)
                        .tasaInteres(new BigDecimal("5.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build(),
                PrestamoResponse.builder()
                        .id(2L)
                        .clienteId(clienteId)
                        .cuentaId(2L)
                        .monto(new BigDecimal("2000.0"))
                        .plazoMeses(24)
                        .tasaInteres(new BigDecimal("6.0"))
                        .fechaDesembolso((Date.valueOf(LocalDate.now())))
                        .build()
        );
        when(clienteClient.getClienteById(1L)).thenReturn(mock(ClienteResponse.class));
        when(prestamoRepository.findByClienteId(clienteId)).thenReturn(prestamoEntities);
        when(prestamoMapper.toDtoList(prestamoEntities)).thenReturn(prestamos);

        List<PrestamoResponse> result = prestamoService.getPrestamosByClienteId(clienteId);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(clienteId, result.getFirst().clienteId()),
                () -> assertEquals(clienteId, result.get(1).clienteId())
        );

        verify(prestamoRepository).findByClienteId(clienteId);
        verify(prestamoMapper).toDtoList(prestamoEntities);
    }
    @Test
    @DisplayName("Prueba de obtener préstamos por ID de cliente ResourceNotFound")
    void getPrestamosByClienteIdResourceNotFoundTest() {
        Long clienteId = 1L;
        when(clienteClient.getClienteById(clienteId)).thenThrow(new ResourceNotFound("Cliente no encontrado con id: " + clienteId));
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> prestamoService.getPrestamosByClienteId(clienteId)
        );
        assertEquals("Cliente no encontrado con id: " + clienteId, exception.getMessage());
        verify(clienteClient).getClienteById(clienteId);
    }
}
