package org.nttdata.com.servicionotificaciones.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ExceptionHandleController;
import org.nttdata.com.servicionotificaciones.service.EstadoNotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EstadoNotificacionControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private EstadoNotificacionController estadoNotificacionController;
    @Mock
    private EstadoNotificacionService estadoNotificacionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(estadoNotificacionController)
                .setControllerAdvice(new ExceptionHandleController())
                .build();
    }

    @Test
    @DisplayName("listar todos los estados de notificacion")
    public void listarTodosLosEstadoNotifiacion() throws Exception{
        List<EstadoNotificacionResponse> estadoNotificacionResponses = List.of(
                EstadoNotificacionResponse.builder()
                        .id(1L)
                        .estado("ENVIADO")
                        .build(),
                EstadoNotificacionResponse.builder()
                        .id(2L)
                        .estado("FALLIDO")
                        .build());

        when(estadoNotificacionService.getAllEstadosNotificacion()).thenReturn(estadoNotificacionResponses);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/estado-notificaciones")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].estado").value("ENVIADO"))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].estado").value("FALLIDO"));
    }


    @Test
    @DisplayName("obtener estado de notificacion por id")
    public void obtenerEstadoNotificacionPorId() throws Exception {
        EstadoNotificacionResponse response = EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build();
        when(estadoNotificacionService.getEstadoNotificacionById(1L)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/estado-notificaciones/1")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    @DisplayName("crear estado de notificacion")
    public void crearEstadoNotificacion() throws Exception {
        EstadoNotificacionRequest request = EstadoNotificacionRequest.builder().estado("ENVIADO").build();
        EstadoNotificacionResponse response = EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build();

        when(estadoNotificacionService.createEstadoNotificacion(request)).thenReturn(response);


        mockMvc.perform(
                        MockMvcRequestBuilders.post("/estado-notificaciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                         "estado":"ENVIADO"
                                    }
                                """)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }
    @Test
    @DisplayName("actualizar estado de notificacion")
    public void actualizarEstadoNotificacion() throws Exception {
        EstadoNotificacionRequest request = EstadoNotificacionRequest.builder().estado("FALLIDO").build();
        EstadoNotificacionResponse response = EstadoNotificacionResponse.builder().id(1L).estado("FALLIDO").build();

        when(estadoNotificacionService.updateEstadoNotificacion(1L, request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/estado-notificaciones/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {"estado":"FALLIDO"}
                                """)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("FALLIDO"));
    }

    @Test
    @DisplayName("eliminar estado de notificacion")
    public void eliminarEstadoNotificacion() throws Exception {
        doNothing().when(estadoNotificacionService).deleteEstadoNotificacion(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/estado-notificaciones/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

}
