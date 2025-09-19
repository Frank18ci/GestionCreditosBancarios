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
import org.nttdata.com.servicioprestamos.exception.ExceptionHandleController;
import org.nttdata.com.servicioprestamos.service.EstadoPrestamoService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EstadoPrestamoControllerTest {
    MockMvc mockMvc;
    @InjectMocks
    private EstadoPrestamoController estadoPrestamoController;
    @Mock
    private EstadoPrestamoService estadoPrestamoService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(estadoPrestamoController)
                .setControllerAdvice(new ExceptionHandleController())
                .build();
    }

    @Test
    @DisplayName("Test para obtener todos los estados de prestamos")
    void testGetAllEstadosPrestamos() throws Exception {
        List<EstadoPrestamoResponse> estados = Arrays.asList(
                EstadoPrestamoResponse.builder().id(1L).nombre("ACTIVO").build(),
                EstadoPrestamoResponse.builder().id(2L).nombre("INACTIVO").build()
        );

        when(estadoPrestamoService.getAllEstadosPrestamo()).thenReturn(estados);

        mockMvc.perform(MockMvcRequestBuilders.get("/estado-prestamos")
                        .accept("application/json"))
                .andExpect(jsonPath("$[0].id").value(1L)).andExpect(jsonPath("$[0].nombre").value("ACTIVO"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("INACTIVO")
                );
    }
    @Test
    @DisplayName("Test para obtener un estado de prestamo por ID")
    void testGetEstadoPrestamoById() throws Exception {
        EstadoPrestamoResponse estado = EstadoPrestamoResponse.builder().id(1L).nombre("ACTIVO").build();

        when(estadoPrestamoService.getEstadoPrestamoById(1L)).thenReturn(estado);

        mockMvc.perform(MockMvcRequestBuilders.get("/estado-prestamos/{id}", 1L)
                        .accept("application/json"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("ACTIVO"));
    }

    @Test
    @DisplayName("Test para crear un estado de prestamo")
    void testCreateEstadoPrestamo() throws Exception {
        EstadoPrestamoResponse estado = EstadoPrestamoResponse.builder().id(1L).nombre("ACTIVO").build();

        when(estadoPrestamoService.createEstadoPrestamo(ArgumentMatchers.any())).thenReturn(estado);

        String nuevoEstadoJson = """
                {
                    "nombre": "ACTIVO"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/estado-prestamos")
                        .contentType("application/json")
                        .content(nuevoEstadoJson)
                        .accept("application/json"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("ACTIVO"));
    }
    @Test
    @DisplayName("Test para actualizar un estado de prestamo")
    void testUpdateEstadoPrestamo() throws Exception {
        EstadoPrestamoResponse estadoActualizado = EstadoPrestamoResponse.builder().id(1L).nombre("INACTIVO").build();
        when(estadoPrestamoService.updateEstadoPrestamo(ArgumentMatchers.eq(1L), ArgumentMatchers.any())).thenReturn(estadoActualizado);
        String estadoActualizadoJson = """
                {
                    "nombre": "INACTIVO"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.put("/estado-prestamos/{id}", 1L)
                        .contentType("application/json")
                        .content(estadoActualizadoJson)
                        .accept("application/json"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("INACTIVO"));
    }
    @Test
    @DisplayName("Test para eliminar un estado de prestamo")
    void testDeleteEstadoPrestamo() throws Exception {
        doNothing().when(estadoPrestamoService).deleteEstadoPrestamo(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/estado-prestamos/{id}", 1L)
                        .accept("application/json"))
                .andExpect(status().isNoContent());
    }
}
