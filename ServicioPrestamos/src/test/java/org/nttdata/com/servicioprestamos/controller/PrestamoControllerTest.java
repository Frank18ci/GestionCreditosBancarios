package org.nttdata.com.servicioprestamos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoResponse;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.ExceptionHandleController;
import org.nttdata.com.servicioprestamos.service.PrestamoService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PrestamoControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private PrestamoController prestamoController;

    @Mock
    private PrestamoService prestamoService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(prestamoController)
                .setControllerAdvice(new ExceptionHandleController())
                .build();
    }
    @Test
    @DisplayName("Obtener todos los prestamos - Exito")
    void listarPrestamos() throws Exception {
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

        when(prestamoService.getAllPrestamos()).thenReturn(prestamos);
        mockMvc.perform(
                MockMvcRequestBuilders.get("/prestamos")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(prestamos.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].clienteId").value(1L))
                .andExpect(jsonPath("$[0].cuentaId").value(1L))
                .andExpect(jsonPath("$[0].monto").value(1000.0))
                .andExpect(jsonPath("$[0].plazoMeses").value(12))
                .andExpect(jsonPath("$[0].tasaInteres").value(5.0))
                .andExpect(jsonPath("$[0].fechaDesembolso").exists())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].clienteId").value(2L))
                .andExpect(jsonPath("$[1].cuentaId").value(2L))
                .andExpect(jsonPath("$[1].monto").value(2000.0))
                .andExpect(jsonPath("$[1].plazoMeses").value(24))
                .andExpect(jsonPath("$[1].tasaInteres").value(6.0))
                .andExpect(jsonPath("$[1].fechaDesembolso").exists()
                );
    }

    @Test
    @DisplayName("Obtener prestamo por ID - Exito")
    void obtenerPrestamoPorId() throws Exception {
        Long id = 1L;
        PrestamoResponse prestamo = PrestamoResponse.builder()
                .id(id)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.0"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.0"))
                .fechaDesembolso((Date.valueOf(LocalDate.now())))
                .build();

        when(prestamoService.getPrestamoById(id)).thenReturn(prestamo);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/prestamos/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.clienteId").value(1L))
        .andExpect(jsonPath("$.cuentaId").value(1L))
        .andExpect(jsonPath("$.monto").value(1000.0))
        .andExpect(jsonPath("$.plazoMeses").value(12))
        .andExpect(jsonPath("$.tasaInteres").value(5.0))
        .andExpect(jsonPath("$.fechaDesembolso").exists());
    }
    @Test
    @DisplayName("Crear prestamo - Exito")
    void crearPrestamo() throws Exception {
        Long id = 1L;
        PrestamoResponse prestamo = PrestamoResponse.builder()
                .id(id)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.0"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.0"))
                .fechaDesembolso((Date.valueOf(LocalDate.now())))
                .build();
        when(prestamoService.createPrestamo(ArgumentMatchers.any())).thenReturn(prestamo);

        mockMvc.perform(MockMvcRequestBuilders.post("/prestamos")
        .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "clienteId": 1,
                            "cuentaId": 1,
                            "monto": 1000.0,
                            "plazoMeses": 12,
                            "tasaInteres": 5.0,
                            "fechaDesembolso": "2024-01-01"
                        }
                        """)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
         .andExpect(jsonPath("$.id").value(id))
         .andExpect(jsonPath("$.clienteId").value(1L))
         .andExpect(jsonPath("$.cuentaId").value(1L))
         .andExpect(jsonPath("$.monto").value(1000.0))
         .andExpect(jsonPath("$.plazoMeses").value(12))
         .andExpect(jsonPath("$.tasaInteres").value(5.0))
         .andExpect(jsonPath("$.fechaDesembolso").exists()
         );
    }
    @Test
    @DisplayName("actualizar prestamo - Exito")
    void actualizarPrestamo() throws Exception {
        Long id = 1L;
        PrestamoResponse prestamo = PrestamoResponse.builder()
                .id(id)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.0"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.0"))
                .fechaDesembolso((Date.valueOf(LocalDate.now())))
                .build();
        when(prestamoService.updatePrestamo(ArgumentMatchers.eq(id), ArgumentMatchers.any())).thenReturn(prestamo);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/prestamos/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "clienteId": 1,
                                    "cuentaId": 1,
                                    "monto": 1000.0,
                                    "plazoMeses": 12,
                                    "tasaInteres": 5.0,
                                    "fechaDesembolso": "2024-01-01"
                                }
                                """)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.cuentaId").value(1L))
                .andExpect(jsonPath("$.monto").value(1000.0))
                .andExpect(jsonPath("$.plazoMeses").value(12))
                .andExpect(jsonPath("$.tasaInteres").value(5.0))
                .andExpect(jsonPath("$.fechaDesembolso").exists()
        );
    }
    @Test
    @DisplayName("Eliminar prestamo - Exito")
    void eliminarPrestamo() throws Exception {
        Long id = 1L;
        doNothing().when(prestamoService).deletePrestamo(id);
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/prestamos/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("Test para aceptar un prestamo")
    void aceptarPrestamoTest() throws Exception {
        Long prestamoId = 1L;
        PrestamoResponse prestamoResponse = PrestamoResponse.builder()
                .id(prestamoId)
                .clienteId(1L)
                .cuentaId(1L)
                .monto(new BigDecimal("1000.00"))
                .plazoMeses(12)
                .tasaInteres(new BigDecimal("5.00"))
                .fechaDesembolso(new java.util.Date())
                .estadoPrestamo(EstadoPrestamoResponse.builder()
                        .id(2L)
                        .nombre("APROBADO")
                        .build())
                .build();
        when(prestamoService.aceptarPrestamo(prestamoId)).thenReturn(prestamoResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/prestamos/aprobar/{id}", prestamoId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(prestamoId))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.cuentaId").value(1L))
                .andExpect(jsonPath("$.monto").value(1000.00))
                .andExpect(jsonPath("$.plazoMeses").value(12))
                .andExpect(jsonPath("$.tasaInteres").value(5.00))
                .andExpect(jsonPath("$.estadoPrestamo.id").value(2L))
                .andExpect(jsonPath("$.estadoPrestamo.nombre").value("APROBADO"));
    }
}
