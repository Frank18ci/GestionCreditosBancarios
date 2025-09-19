package org.nttdata.com.servicionotificaciones.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.NotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.NotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.exception.ExceptionHandleController;
import org.nttdata.com.servicionotificaciones.service.NotificacionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificacionControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private NotificacionController notificacionController;
    @Mock
    private NotificacionService notificacionService;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(notificacionController)
                .setControllerAdvice(new ExceptionHandleController())
                .build();
    }

    @Test
    @DisplayName("listar todas las notificaciones")
    public void listarTodasLasNotificaciones() throws Exception {
        List<NotificacionResponse> responses = List.of(
                NotificacionResponse.builder()
                        .id(1L)
                        .mensaje("Mensaje 1")
                        .estadoNotificacion(EstadoNotificacionResponse.builder()
                                .id(1L)
                                .estado("ENVIADO")
                                .build())
                        .asunto("Asunto 1")
                        .tipoNotificacion(TipoNotificacionResponse.builder()
                                .id(1L)
                                .nombre("EMAIL")
                                .build())
                        .fechaEnvio(new Date())
                        .clienteId(1L)
                        .build(),
                NotificacionResponse.builder()
                        .id(2L)
                        .mensaje("Mensaje 2")
                        .estadoNotificacion(EstadoNotificacionResponse.builder()
                                .id(2L)
                                .estado("FALLIDO")
                                .build())
                        .asunto("Asunto 2")
                        .tipoNotificacion(TipoNotificacionResponse.builder()
                                .id(2L)
                                .nombre("SMS")
                                .build())
                        .fechaEnvio(new Date())
                        .clienteId(2L)
                        .build()
        );

        when(notificacionService.getAllNotificaciones()).thenReturn(responses);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/notificaciones")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estadoNotificacion.id").value(1L))
                .andExpect(jsonPath("$[0].estadoNotificacion.estado").value("ENVIADO"))
                .andExpect(jsonPath("$[0].asunto").value("Asunto 1"))
                .andExpect(jsonPath("$[0].tipoNotificacion.id").value(1L))
                .andExpect(jsonPath("$[0].tipoNotificacion.nombre").value("EMAIL"))
                .andExpect(jsonPath("$[0].clienteId").value(1L))
                .andExpect(jsonPath("$[0].fechaEnvio").exists())
                .andExpect(jsonPath("$[0].mensaje").value("Mensaje 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].mensaje").value("Mensaje 2"))
                .andExpect(jsonPath("$[1].estadoNotificacion.id").value(2L))
                .andExpect(jsonPath("$[1].estadoNotificacion.estado").value("FALLIDO"))
                .andExpect(jsonPath("$[1].asunto").value("Asunto 2"))
                .andExpect(jsonPath("$[1].tipoNotificacion.id").value(2L))
                .andExpect(jsonPath("$[1].tipoNotificacion.nombre").value("SMS"))
                .andExpect(jsonPath("$[1].clienteId").value(2L))
                .andExpect(jsonPath("$[1].fechaEnvio").exists());
    }

    @Test
    @DisplayName("obtener notificacion por id")
    public void obtenerNotificacionPorId() throws Exception {
        NotificacionResponse response = NotificacionResponse.builder()
                                            .id(1L)
                                            .mensaje("Mensaje 1")
                                            .estadoNotificacion(EstadoNotificacionResponse.builder()
                                                    .id(1L)
                                                    .estado("ENVIADO")
                                                    .build())
                                            .asunto("Asunto 1")
                                            .tipoNotificacion(TipoNotificacionResponse.builder()
                                                    .id(1L)
                                                    .nombre("EMAIL")
                                                    .build())
                                            .fechaEnvio(new Date())
                                            .clienteId(1L)
                                            .build();
        when(notificacionService.getNotificacionById(1L)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/notificaciones/1")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mensaje").value("Mensaje 1"))
                .andExpect(jsonPath("$.estadoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.estadoNotificacion.estado").value("ENVIADO"))
                .andExpect(jsonPath("$.asunto").value("Asunto 1"))
                .andExpect(jsonPath("$.tipoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.tipoNotificacion.nombre").value("EMAIL"))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.fechaEnvio").exists());
    }

    @Test
    @DisplayName("crear notificacion")
    public void crearNotificacion() throws Exception {
        NotificacionRequest request = NotificacionRequest.builder()
                                        .mensaje("Mensaje nuevo")
                                        .estadoNotificacionId(1L)
                                        .asunto("Asunto 1")
                                        .tipoNotificacionId(1L)
                                        .clienteId(1L)
                                        .build();
        NotificacionResponse response = NotificacionResponse.builder()
                                            .id(1L)
                                            .mensaje("Mensaje nuevo")
                                            .estadoNotificacion(EstadoNotificacionResponse.builder()
                                                    .id(1L)
                                                    .estado("ENVIADO")
                                                    .build())
                                            .asunto("Asunto 1")
                                            .tipoNotificacion(TipoNotificacionResponse.builder()
                                                    .id(1L)
                                                    .nombre("EMAIL")
                                                    .build())
                                            .fechaEnvio(new Date())
                                            .clienteId(1L)
                                            .build();

        when(notificacionService.createNotificacion(request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/notificaciones")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(""" 
                                    {
                                                "mensaje":"Mensaje nuevo", "estadoNotificacionId":1, "asunto":"Asunto 1", "tipoNotificacionId":1, "clienteId":1
                                                                        }""")
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mensaje").value("Mensaje nuevo"))
                .andExpect(jsonPath("$.estadoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.estadoNotificacion.estado").value("ENVIADO"))
                .andExpect(jsonPath("$.asunto").value("Asunto 1"))
                .andExpect(jsonPath("$.tipoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.tipoNotificacion.nombre").value("EMAIL"))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.fechaEnvio").exists());
    }

    @Test
    @DisplayName("actualizar notificacion")
    public void actualizarNotificacion() throws Exception {
        NotificacionRequest request = NotificacionRequest.builder()
                .mensaje("Mensaje actualizado")
                .estadoNotificacionId(1L)
                .asunto("Asunto 1")
                .tipoNotificacionId(1L)
                .clienteId(1L)
                .build();
        NotificacionResponse response = NotificacionResponse.builder()
                .id(1L)
                .mensaje("Mensaje actualizado")
                .estadoNotificacion(EstadoNotificacionResponse.builder()
                        .id(1L)
                        .estado("ENVIADO")
                        .build())
                .asunto("Asunto 1")
                .tipoNotificacion(TipoNotificacionResponse.builder()
                        .id(1L)
                        .nombre("EMAIL")
                        .build())
                .fechaEnvio(new Date())
                .clienteId(1L)
                .build();

        when(notificacionService.updateNotificacion(1L, request)).thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/notificaciones/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {"mensaje":"Mensaje actualizado", "estadoNotificacionId":1, "asunto":"Asunto 1", "tipoNotificacionId":1, "clienteId":1}
                        """)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mensaje").value("Mensaje actualizado"))
                .andExpect(jsonPath("$.estadoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.estadoNotificacion.estado").value("ENVIADO"))
                .andExpect(jsonPath("$.asunto").value("Asunto 1"))
                .andExpect(jsonPath("$.tipoNotificacion.id").value(1L))
                .andExpect(jsonPath("$.tipoNotificacion.nombre").value("EMAIL"))
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.fechaEnvio").exists());
    }

    @Test
    @DisplayName("eliminar notificacion")
    public void eliminarNotificacion() throws Exception {
        doNothing().when(notificacionService).deleteNotificacion(1L);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/notificaciones/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

}
