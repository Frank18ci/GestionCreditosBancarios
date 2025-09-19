package org.nttdata.com.servicioprestamos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaResponse;
import org.nttdata.com.servicioprestamos.exception.ExceptionHandleController;
import org.nttdata.com.servicioprestamos.service.EstadoCuotaService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EstadoCuotaControllerTest {
    MockMvc mockMvc;
    @InjectMocks
    EstadoCuotaController estadoCuotaController;
    @Mock
    EstadoCuotaService estadoCuotaService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(estadoCuotaController)
                .setControllerAdvice(new ExceptionHandleController())
                .build();
    }

    @Test
    @DisplayName("Test para obtener todos los estados de cuotas")
    void testGetAllEstadosCuotas() throws Exception {
        List<EstadoCuotaResponse> estados = List.of(
                EstadoCuotaResponse.builder().id(1L).nombre("PENDIENTE").build(),
                EstadoCuotaResponse.builder().id(2L).nombre("PAGADO").build()
        );
        when(estadoCuotaService.getAllEstadosCuota()).thenReturn(estados);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/estado-cuotas")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("PAGADO"));
    }

    @Test
    @DisplayName("Test para obtener un estado de cuota por ID")
    void testGetEstadoCuotaById() throws Exception {
        EstadoCuotaResponse estado = EstadoCuotaResponse.builder().id(1L).nombre("PENDIENTE").build();
        when(estadoCuotaService.getEstadoCuotaById(1L)).thenReturn(estado);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/estado-cuotas/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Test para crear un estado de cuota")
    void testCrearEstadoCuota() throws Exception {
        EstadoCuotaResponse estado = EstadoCuotaResponse.builder().id(1L).nombre("PENDIENTE").build();
        when(estadoCuotaService.saveEstadoCuota(ArgumentMatchers.any()))
                .thenReturn(estado);
        String estadoCuotaJson = """
                {
                    "nombre": "PENDIENTE"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/estado-cuotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoCuotaJson)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Test para actualizar un estado de cuota")
    void testActualizarEstadoCuota() throws Exception {
        EstadoCuotaResponse estado = EstadoCuotaResponse.builder().id(1L).nombre("PAGADO").build();
        when(estadoCuotaService.updateEstadoCuota(ArgumentMatchers.eq(1L), ArgumentMatchers.any()))
                .thenReturn(estado);
        String estadoCuotaJson = """
                {
                    "nombre": "PAGADO"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/estado-cuotas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoCuotaJson)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("PAGADO"));
    }

    @Test
    @DisplayName("Test para eliminar un estado de cuota")
    void testEliminarEstadoCuota() throws Exception {

        doNothing().when(estadoCuotaService).deleteEstadoCuota(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/estado-cuotas/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }
}
