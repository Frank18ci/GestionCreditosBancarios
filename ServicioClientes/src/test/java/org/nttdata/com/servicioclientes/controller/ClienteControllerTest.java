package org.nttdata.com.servicioclientes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.exception.GlobalExceptionHandler;
import org.nttdata.com.servicioclientes.service.ClienteService;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private ClienteController clienteController;

    @Mock
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Debe retornar todos los clientes")
    void obtenerClientes() throws Exception {
        List<ClienteResponse> clientes = List.of(
                ClienteResponse.builder()
                        .id(1L)
                        .nombre("Juan Perez")
                        .email("juanperez@gmail.com")
                        .dni("12345678")
                        .keycloakId("abc123")
                        .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build()).build(),
                ClienteResponse.builder()
                        .id(2L)
                        .nombre("Maria Gomez")
                        .email("mariagomez@gmail.com")
                        .dni("87654321")
                        .keycloakId("def456")
                        .estadoCliente(EstadoClienteResponse.builder().id(2L).estado("INACTIVO").build()).build()

        );
        when(clienteService.obtenerTodosClientes()).thenReturn(clientes);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/clientes")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(clientes.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$[0].email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$[0].dni").value("12345678"))
                .andExpect(jsonPath("$[0].keycloakId").value("abc123"))
                .andExpect(jsonPath("$[0].estadoCliente.id").value(1L))
                .andExpect(jsonPath("$[0].estadoCliente.estado").value("ACTIVO"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("Maria Gomez"))
                .andExpect(jsonPath("$[1].email").value("mariagomez@gmail.com"))
                .andExpect(jsonPath("$[1].dni").value("87654321"))
                .andExpect(jsonPath("$[1].keycloakId").value("def456"))
                .andExpect(jsonPath("$[1].estadoCliente.id").value(2L))
                .andExpect(jsonPath("$[1].estadoCliente.estado").value("INACTIVO"));
    }

    @Test
    @DisplayName("Debe retornar un cliente por ID")
    void obtenerClientePorId() throws Exception {
        Long id = 1L;
        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nombre("Juan Perez")
                .email("juanperez@gmail.com")
                .dni("12345678")
                .keycloakId("abc123")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();

        when(clienteService.obtenerClientePorId(id)).thenReturn(cliente);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/clientes/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.keycloakId").value("abc123"))
                .andExpect(jsonPath("$.estadoCliente.id").value(1L))
                .andExpect(jsonPath("$.estadoCliente.estado").value("ACTIVO"));
    }
    @Test
    @DisplayName("Debe retornar clientes con estadoClienteId")
    void obtenerClientesPorEstadoClienteId() throws Exception {

        Long estadoClienteId = 1L;
        List<ClienteResponse> clientes = List.of(
                ClienteResponse.builder()
                        .id(1L)
                        .nombre("Juan Perez")
                        .email("juanperez@gmail.com")
                        .dni("12345678")
                        .keycloakId("abc123")
                        .estadoCliente(EstadoClienteResponse.builder().id(estadoClienteId).estado("ACTIVO").build()).build(),
                ClienteResponse.builder()
                        .id(2L)
                        .nombre("Carlos Ruiz")
                        .email("carlosruiz@gmail.com")
                        .dni("11223344")
                        .keycloakId("ghi789")
                        .estadoCliente(EstadoClienteResponse.builder().id(estadoClienteId).estado("ACTIVO").build()).build()
        );
        when(clienteService.obtenerClientesPorEstado(estadoClienteId)).thenReturn(clientes);

        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/estado/{estadoClienteId}", estadoClienteId)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(clientes.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$[0].email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$[0].dni").value("12345678"))
                .andExpect(jsonPath("$[0].keycloakId").value("abc123"))
                .andExpect(jsonPath("$[0].estadoCliente.id").value(estadoClienteId))
                .andExpect(jsonPath("$[0].estadoCliente.estado").value("ACTIVO"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("Carlos Ruiz"))
                .andExpect(jsonPath("$[1].email").value("carlosruiz@gmail.com"))
                .andExpect(jsonPath("$[1].dni").value("11223344"))
                .andExpect(jsonPath("$[1].keycloakId").value("ghi789"))
                .andExpect(jsonPath("$[1].estadoCliente.id").value(estadoClienteId))
                .andExpect(jsonPath("$[1].estadoCliente.estado").value("ACTIVO"));
    }
    @Test
    @DisplayName("Debe crear un nuevo cliente y retornar el cliente creado")
    void crearCliente() throws Exception {
        Long id = 1L;
        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .dni("99887766")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();
        when(clienteService.crearCliente(
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(cliente);
        mockMvc.perform(MockMvcRequestBuilders.post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Franklin Carpio",
                                    "email": "franklincarpio@gmail.com",
                                    "dni": "99887766",
                                    "estadoClienteId": 1
                                }"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.nombre").value("Franklin Carpio"))
        .andExpect(jsonPath("$.email").value("franklincarpio@gmail.com"))
        .andExpect(jsonPath("$.dni").value("99887766"))
        .andExpect(jsonPath("$.estadoCliente.id").value(1L))
        .andExpect(jsonPath("$.estadoCliente.estado").value("ACTIVO"));
    }
    @Test
    @DisplayName("Debe actualizar un cliente y retornar el cliente actualizado")
    void updateCliente() throws Exception {
        Long id = 1L;
        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .dni("99887766")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();
        when(clienteService.actualizarCliente(
                org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(cliente);
        mockMvc.perform(MockMvcRequestBuilders.put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Franklin Carpio",
                                    "email": "franklincarpio@gmail.com",
                                    "dni": "99887766",
                                    "estadoClienteId": 1
                                }"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Franklin Carpio"))
                .andExpect(jsonPath("$.email").value("franklincarpio@gmail.com"))
                .andExpect(jsonPath("$.dni").value("99887766"))
                .andExpect(jsonPath("$.estadoCliente.id").value(1L))
                .andExpect(jsonPath("$.estadoCliente.estado").value("ACTIVO"));
    }
    @Test
    @DisplayName("Debe eliminar un cliente y retornar 204")
    void eliminarCliente() throws Exception {
        Long id = 1L;

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/clientes/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debe buscar clientes por nombre containing y retornar la lista de clientes")
    void buscarClientesPorNombreContaining() throws Exception {
        String nombre = "Juan";
        List<ClienteResponse> clientes = List.of(
                ClienteResponse.builder()
                        .id(1L)
                        .nombre("Juan Perez")
                        .email("juanperez@gmail.com")
                        .dni("12345678")
                        .keycloakId("abc123")
                        .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build()).build(),
                ClienteResponse.builder()
                        .id(2L)
                        .nombre("Juan Gomez")
                        .email("juangomez@gmail.com")
                        .dni("87654321")
                        .keycloakId("def456")
                        .estadoCliente(EstadoClienteResponse.builder().id(2L).estado("INACTIVO").build()).build()
        );
        when(clienteService.buscarPorNombre(nombre)).thenReturn(clientes);

        mockMvc.perform(MockMvcRequestBuilders.get("/clientes/buscar")
                        .param("nombre", nombre)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(clientes.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$[0].email").value("juanperez@gmail.com"))
                .andExpect(jsonPath("$[0].dni").value("12345678"))
                .andExpect(jsonPath("$[0].keycloakId").value("abc123"))
                .andExpect(jsonPath("$[0].estadoCliente.id").value(1L))
                .andExpect(jsonPath("$[0].estadoCliente.estado").value("ACTIVO"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nombre").value("Juan Gomez"))
                .andExpect(jsonPath("$[1].email").value("juangomez@gmail.com"))
                .andExpect(jsonPath("$[1].dni").value("87654321"))
                .andExpect(jsonPath("$[1].keycloakId").value("def456"))
                .andExpect(jsonPath("$[1].estadoCliente.id").value(2L))
                .andExpect(jsonPath("$[1].estadoCliente.estado").value("INACTIVO"));
    }
    @Test
    @DisplayName("Debe verificar la existencia de un cliente por ID y retornar true")
    void verificarExistenciaCliente() throws Exception {
        Long id = 1L;
        when(clienteService.existeCliente(id)).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/clientes/existe/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$").value(true));
    }

    @Test
    @DisplayName("Debe registrar el keycloakId de un cliente y retornar el cliente actualizado")
    void registrarKeycloakId() throws Exception {
        Long id = 1L;
        String keycloakId = "keycloak-123";

        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .dni("99887766")
                .keycloakId(keycloakId)
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();

        when(clienteService.registerKeyCloakId(id, keycloakId)).thenReturn(cliente);

        // Crear un JWT simulado con el subject como keycloakId
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .subject(keycloakId)
                .build();
        // Crear una autenticación simulada con el JWT
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/register-keycloak/{id}", id)
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId));
    }


    @Test
    @DisplayName("Debe actualizar el keycloakId de un cliente y retornar el cliente actualizado")
    void actualizarKeycloakId() throws Exception {
        Long id = 1L;
        String keycloakId = "keycloak-123";

        ClienteResponse cliente = ClienteResponse.builder()
                .id(id)
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .dni("99887766")
                .keycloakId(keycloakId)
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();

        when(clienteService.updateKeyCloakId(id, keycloakId)).thenReturn(cliente);

        // Crear un JWT simulado con el subject como keycloakId
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .subject(keycloakId)
                .build();
        // Crear una autenticación simulada con el JWT
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);

        mockMvc.perform(MockMvcRequestBuilders.post("/clientes/update-keycloak/{id}", id)
                        .principal(auth)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId));
    }


}
