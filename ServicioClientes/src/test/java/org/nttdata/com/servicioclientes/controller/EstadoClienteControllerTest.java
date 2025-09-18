package org.nttdata.com.servicioclientes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.exception.GlobalExceptionHandler;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.service.EstadoClienteService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class EstadoClienteControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EstadoClienteController estadoClienteController;

    @Mock
    private EstadoClienteService estadoClienteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(estadoClienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Debe retornar todos los estados de clientes")
    void obtenerAllEstadosClientes() throws Exception {
        List<EstadoClienteResponse> estadosClientes = List.of(
                EstadoClienteResponse.builder().id(1L).estado("Activo").build(),
                EstadoClienteResponse.builder().id(2L).estado("Inactivo").build()
        );

        when(estadoClienteService.obtenerTodosLosEstados()).thenReturn(estadosClientes);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/estado-clientes")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.size()").value(estadosClientes.size()))
         .andExpect(jsonPath("$[0].id").value(1L))
         .andExpect(jsonPath("$[0].estado").value("Activo"))
         .andExpect(jsonPath("$[1].id").value(2L))
         .andExpect(jsonPath("$[1].estado").value("Inactivo")
        );
    }

    @Test
    @DisplayName("Debe retornar un estado de cliente por ID")
    void obtenerEstadoClientePorId() throws Exception {
        Long id = 1L;
        EstadoClienteResponse estadoCliente = EstadoClienteResponse.builder()
                .id(id)
                .estado("Activo")
                .build();

        when(estadoClienteService.obtenerEstadoPorId(id)).thenReturn(estadoCliente);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/estado-clientes/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(id))
         .andExpect(jsonPath("$.estado").value("Activo"));
    }
    @Test
    @DisplayName("Debe registrar un nuevo estado de cliente y retornar el estado creado")
    void registrarEstadoCliente() throws Exception {
        Long id = 1L;
        EstadoClienteResponse estadoCliente = EstadoClienteResponse.builder()
                .id(id)
                .estado("Activo")
                .build();

        when(estadoClienteService.crearEstado(
                ArgumentMatchers.any()
        )).thenReturn(estadoCliente);

        String nuevoEstadoJson = """
                {
                    "estado": "Activo"
                }
                """;

        mockMvc.perform(
                MockMvcRequestBuilders.post("/estado-clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevoEstadoJson)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
         .andExpect(jsonPath("$.id").value(id))
         .andExpect(jsonPath("$.estado").value("Activo"));
    }
    @Test
    @DisplayName("Debe actualizar un estado de cliente existente y retornar el estado actualizado")
    void actualizarEstadoCliente() throws Exception {
        Long id = 1L;
        EstadoClienteResponse estadoCliente = EstadoClienteResponse.builder()
                .id(id)
                .estado("Inactivo")
                .build();

        when(estadoClienteService.actualizarEstado(
                ArgumentMatchers.eq(id),
                ArgumentMatchers.any()
        )).thenReturn(estadoCliente);

        String estadoActualizadoJson = """
                {
                    "estado": "Inactivo"
                }
                """;

        mockMvc.perform(
                MockMvcRequestBuilders.put("/estado-clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoActualizadoJson)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.id").value(id))
         .andExpect(jsonPath("$.estado").value("Inactivo"));
    }
    @Test
    @DisplayName("Debe eliminar un estado de cliente existente y retornar 204")
    void eliminarEstadoCliente() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/estado-clientes/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("Debe retornar 404 al obtener un estado de cliente que no existe")
    void eliminarEstadoCliente_NotExits() throws Exception {
        Long id = 99L;

        doThrow(new ResourceNotFound("Estado de cliente no encontrado con id: " + id))
                .when(estadoClienteService).eliminarEstado(id);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/estado-clientes/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }
}
