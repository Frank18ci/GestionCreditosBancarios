package org.nttdata.com.servicionotificaciones.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ExceptionHandleController;
import org.nttdata.com.servicionotificaciones.service.TipoNotificacionService;
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
public class TipoNotificacionControllerTest {
    private MockMvc mockMvc;
    @InjectMocks TipoNotificacionController tipoNotificacionController;
    @Mock
    private TipoNotificacionService tipoNotificacionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tipoNotificacionController).setControllerAdvice(new ExceptionHandleController()).build();
    }

    @Test
    @DisplayName("listar todos los tipos de notificacion")
    public void listarTodosLosTiposNotificacion() throws Exception {
        List<TipoNotificacionResponse> responses = List.of(
                TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build(),
                TipoNotificacionResponse.builder().id(2L).nombre("SMS").build()
        );
        when(tipoNotificacionService.getAllTiposNotificacion()).thenReturn(responses);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/tipo-notificaciones")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("EMAIL"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("SMS"));
    }

    @Test
    @DisplayName("obtener tipo de notificacion por id")
    public void obtenerTipoNotificacionPorId() throws Exception {
        TipoNotificacionResponse response = TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build();
        when(tipoNotificacionService.getTipoNotificacionById(1L)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/tipo-notificaciones/1")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("EMAIL"));
    }

    @Test
    @DisplayName("crear tipo de notificacion")
    public void crearTipoNotificacion() throws Exception {
        TipoNotificacionRequest request = TipoNotificacionRequest.builder().nombre("EMAIL").build();
        TipoNotificacionResponse response = TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build();

        when(tipoNotificacionService.saveTipoNotificacion(request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/tipo-notificaciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {"nombre":"EMAIL"}
                        """)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("EMAIL"));
    }

    @Test
    @DisplayName("actualizar tipo de notificacion")
    public void actualizarTipoNotificacion() throws Exception {
        TipoNotificacionRequest request = TipoNotificacionRequest.builder().nombre("SMS").build();
        TipoNotificacionResponse response = TipoNotificacionResponse.builder().id(1L).nombre("SMS").build();

        when(tipoNotificacionService.updateTipoNotificacion(1L, request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/tipo-notificaciones/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {"nombre":"SMS"}
                        """)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("SMS"));
    }

    @Test
    @DisplayName("eliminar tipo de notificacion")
    public void eliminarTipoNotificacion() throws Exception {
        doNothing().when(tipoNotificacionService).deleteTipoNotificacion(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/tipo-notificaciones/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
}
