package org.nttdata.com.servicioprestamos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.dto.CuotaResponse;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaResponse;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoResponse;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.ExceptionHandleController;
import org.nttdata.com.servicioprestamos.service.CuotaService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CuotaControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private CuotaController cuotaController;
    @Mock
    private CuotaService cuotaService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(cuotaController)
                .setControllerAdvice(ExceptionHandleController.class)
                .build();
    }
    @Test
    @DisplayName("Test para obtener todas las cuotas de un prestamo")
    void testGetCuotasByPrestamoId() throws Exception {
        List<CuotaResponse> cuotaResponses = List.of(
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
        when(cuotaService.getAllCuotas()).thenReturn(cuotaResponses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/cuotas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].numero").value(1))
                .andExpect(jsonPath("$[0].monto").value(100.00))
                .andExpect(jsonPath("$[0].estadoCuota.id").value(1L))
                .andExpect(jsonPath("$[0].estadoCuota.nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].prestamo.id").value(1L))
                .andExpect(jsonPath("$[0].prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$[0].prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$[0].prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$[0].prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$[0].prestamo.estadoPrestamo.nombre").value("ACTIVO"))

                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].numero").value(2))
                .andExpect(jsonPath("$[1].monto").value(100.00))
                .andExpect(jsonPath("$[1].estadoCuota.id").value(2L))
                .andExpect(jsonPath("$[1].estadoCuota.nombre").value("PAGADO"))
                .andExpect(jsonPath("$[1].prestamo.id").value(1L))
                .andExpect(jsonPath("$[1].prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$[1].prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$[1].prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$[1].prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$[1].prestamo.estadoPrestamo.nombre").value("ACTIVO"));
    }
    @Test
    @DisplayName("Test para obtener una cuota por ID")
    void testGetCuotaById() throws Exception {
        CuotaResponse cuotaResponse =   CuotaResponse.builder()
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
        when(cuotaService.getCuotaById(1L)).thenReturn(cuotaResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/cuotas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.monto").value(100.00))
                .andExpect(jsonPath("$.estadoCuota.id").value(1L))
                .andExpect(jsonPath("$.estadoCuota.nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$.prestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$.prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$.prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.nombre").value("ACTIVO"));
    }
    @Test
    @DisplayName("test para crear una cuota")
    void testCreateCuota() throws Exception {
        CuotaResponse cuotaResponse =   CuotaResponse.builder()
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
        when(cuotaService.saveCuota(ArgumentMatchers.any())).thenReturn(cuotaResponse);
        String cuotaJson = """
                {
                    "numero": 1,
                    "monto": 100.00,
                    "fechaVencimiento": "2024-12-31",
                    "estadoCuotaId": 1,
                    "prestamoId": 1
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/cuotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuotaJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.monto").value(100.00))
                .andExpect(jsonPath("$.estadoCuota.id").value(1L))
                .andExpect(jsonPath("$.estadoCuota.nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$.prestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$.prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$.prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.nombre").value("ACTIVO"));
    }
    @Test
    @DisplayName("test para actualizar una cuota")
    void testUpdateCuota() throws Exception {
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
        when(cuotaService.updateCuota(ArgumentMatchers.eq(1L), ArgumentMatchers.any())).thenReturn(cuotaResponse);
        String cuotaJson = """
                {
                    "numero": 1,
                    "monto": 100.00,
                    "fechaVencimiento": "2024-12-31",
                    "estadoCuotaId": 1,
                    "prestamoId": 1
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/cuotas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuotaJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.monto").value(100.00))
                .andExpect(jsonPath("$.estadoCuota.id").value(1L))
                .andExpect(jsonPath("$.estadoCuota.nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$.prestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$.prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$.prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.nombre").value("ACTIVO"));
    }
    @Test
    @DisplayName("Test para eliminar una cuota")
    void testDeleteCuota() throws Exception {
        doNothing().when(cuotaService).deleteCuota(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/cuotas/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("Test para obtener las cuotas por prestamoId")
    void obtenerCuotasPorPrestamoIdTest() throws Exception {
        Long prestamoId = 1L;
        List<CuotaResponse> cuotaResponses = List.of(
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
        when(cuotaService.getCuotasByPrestamoId(prestamoId)).thenReturn(cuotaResponses);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/cuotas/prestamo/{prestamoId}", prestamoId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].numero").value(1))
                .andExpect(jsonPath("$[0].monto").value(100.00))
                .andExpect(jsonPath("$[0].estadoCuota.id").value(1L))
                .andExpect(jsonPath("$[0].estadoCuota.nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].prestamo.id").value(prestamoId))
                .andExpect(jsonPath("$[0].prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$[0].prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$[0].prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$[0].prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$[0].prestamo.estadoPrestamo.nombre").value("ACTIVO"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].numero").value(2))
                .andExpect(jsonPath("$[1].monto").value(100.00))
                .andExpect(jsonPath("$[1].estadoCuota.id").value(2L))
                .andExpect(jsonPath("$[1].estadoCuota.nombre").value("PAGADO"))
                .andExpect(jsonPath("$[1].prestamo.id").value(prestamoId))
                .andExpect(jsonPath("$[1].prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$[1].prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$[1].prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$[1].prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$[1].prestamo.estadoPrestamo.nombre").value("ACTIVO")
                );
    }
    @Test
    @DisplayName("Test para pagar una cuota")
    void pagarCuotaTest() throws Exception {
        Long cuotaId = 1L;
        Long cuentaId = 1L;
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
        when(cuotaService.pagarCuota(cuotaId, cuentaId)).thenReturn(cuotaResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/cuotas/pagar/{cuentaId}/{id}", cuentaId, cuotaId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cuotaId))
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.monto").value(100.00))
                .andExpect(jsonPath("$.estadoCuota.id").value(2L))
                .andExpect(jsonPath("$.estadoCuota.nombre").value("PAGADO"))
                .andExpect(jsonPath("$.prestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.monto").value(1000.00))
                .andExpect(jsonPath("$.prestamo.plazoMeses").value(12))
                .andExpect(jsonPath("$.prestamo.tasaInteres").value(5.00))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.id").value(1L))
                .andExpect(jsonPath("$.prestamo.estadoPrestamo.nombre").value("ACTIVO"));
    }

}
