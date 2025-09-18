package org.nttdata.com.servicioclientes.service;

import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioclientes.client.CuentaClient;
import org.nttdata.com.servicioclientes.client.dto.CuentaResponse;
import org.nttdata.com.servicioclientes.client.dto.EstadoCuentaResponse;
import org.nttdata.com.servicioclientes.client.dto.TipoCuentaResponse;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.exception.BadRequest;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.model.Cliente;
import org.nttdata.com.servicioclientes.model.EstadoCliente;
import org.nttdata.com.servicioclientes.repository.ClienteRepository;
import org.nttdata.com.servicioclientes.service.impl.ClienteServiceImpl;
import org.nttdata.com.servicioclientes.util.ClienteMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private CuentaClient cuentaClient;

    @InjectMocks
    private ClienteServiceImpl clienteService;



    @Test
    @DisplayName("Debe lanzar ResourceNotFound cuando el cliente no existe al obtener por ID")
    void obtenerClientePorId_throwsExceptionWhenClienteNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> clienteService.obtenerClientePorId(1L));
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
    }

    @Test
    void obteneClientePorId_success() {
        Long clienteId = 1L;
        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frank@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();

        ClienteResponse clienteResponse = ClienteResponse.builder()
                .id(1L)
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frank@gmail.com")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDto(cliente)).thenReturn(clienteResponse);

        ClienteResponse result = clienteService.obtenerClientePorId(clienteId);

        assertEquals(clienteResponse, result);
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteMapper, times(1)).toDto(cliente);
    }

    @Test
    @DisplayName("Actualizar cliente")
    void actualizarCliente_success() {
        Long clienteId = 1L;
        Cliente existingCliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .estadoClienteId(1L)
                .build();
        Cliente updatedCliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        ClienteResponse updatedClienteResponse = ClienteResponse.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(existingCliente));
        when(clienteMapper.toEntity(clienteRequest)).thenReturn(updatedCliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(updatedCliente);
        when(clienteMapper.toDto(updatedCliente)).thenReturn(updatedClienteResponse);

        ClienteResponse result = clienteService.actualizarCliente(clienteId, clienteRequest);
        assertEquals(updatedClienteResponse, result);
        verify(clienteRepository, times(1)).findById(clienteId);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteMapper, times(1)).toDto(updatedCliente);
        verify(clienteMapper, times(1)).toEntity(clienteRequest);
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound cuando el cliente no existe al actualizar")
    void actualizarCliente_throwsExceptionWhenClienteNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .estadoClienteId(1L)
                .build();

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> clienteService.actualizarCliente(1L, clienteRequest));
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
    }
    @Test
    @DisplayName("Debe lanzar BadRequest cuando el DNI ya existe al actualizar")
    void actualizarCliente_throwsExceptionWhenDniExists() {
        Cliente clienteExist = Cliente.builder()
                .id(2L)
                .dni("12345678")
                .nombre("Juan Garcia")
                .email("juangarcia@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        Long idClienteActualizar = 1L;
        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Franklin Carpio")
                .email("franklincarpio@gmail.com")
                .estadoClienteId(1L)
                .build();
        when(clienteRepository.findByDni(clienteRequest.dni())).thenReturn(Optional.of(clienteExist));

        when(clienteRepository.findById(idClienteActualizar))
                .thenReturn(Optional.of(Cliente.builder()
                        .id(idClienteActualizar)
                        .dni("99999999")
                        .nombre("Antiguo Nombre")
                        .email("old@email.com")
                        .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                        .build()
                ));
        assertThrows(BadRequest.class, () -> {
            clienteService.actualizarCliente(idClienteActualizar, clienteRequest);
        });
        verify(clienteRepository).findByDni(clienteRequest.dni());
        verify(clienteRepository).findById(idClienteActualizar);
        verify(clienteRepository, never()).save(any());
    }


    @Test
    @DisplayName("Debe lanzar ResourceNotFound cuando el cliente no existe al obtener con cuentas")
    void obtenerClienteConCuentas_throwsExceptionWhenClienteNotFound() {
        when(clienteRepository.existsById(1L)).thenReturn(false);
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> clienteService.obtenerClienteConCuentas(1L));
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
    }
    @Test
    @DisplayName("Exist client by Id")
    void existeClientePorId_success() {
        Long clienteId = 1L;
        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        boolean exists = clienteService.existeCliente(clienteId);
        assertTrue(exists);
        verify(clienteRepository, times(1)).existsById(clienteId);
    }

    @Test
    @DisplayName("Debe eliminar cliente exitosamente")
    void eliminarCliente_success() {
        Long clienteId = 1L;

        when(clienteRepository.existsById(clienteId))
                .thenReturn(true)
                .thenReturn(false);

        clienteService.eliminarCliente(clienteId);

        verify(clienteRepository, times(2)).existsById(clienteId);
        verify(clienteRepository).deleteById(clienteId);
        verify(clienteRepository).flush();
    }

    @Test
    @DisplayName("Debe lanzar excepción si el cliente no existe")
    void eliminarCliente_notFound() {
        Long clienteId = 2L;

        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                clienteService.eliminarCliente(clienteId)
        );

        assertTrue(exception.getMessage().contains("Cliente no encontrado"));
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción si ocurre error al eliminar")
    void eliminarCliente_deleteError() {
        Long clienteId = 3L;

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        doThrow(new RuntimeException("Fallo en BD"))
                .when(clienteRepository).deleteById(clienteId);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                clienteService.eliminarCliente(clienteId)
        );

        assertTrue(exception.getMessage().contains("Error al eliminar cliente"));
        verify(clienteRepository).deleteById(clienteId);
    }

    @Test
    @DisplayName("Obtener cliente con cuentas exitosamente")
    void obtenerClienteConCuentas_success() {
        Long clienteId = 1L;

        List<CuentaResponse> cuentaResponses = List.of(
                CuentaResponse.builder()
                        .id(1L)
                        .clienteId(clienteId)
                        .tipoCuenta(TipoCuentaResponse.builder().id(1L).nombre("AHoRROS").build())
                        .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVO").build())
                        .saldo(new BigDecimal("1000.0"))
                        .build(),
                CuentaResponse.builder()
                        .id(2L)
                        .clienteId(clienteId)
                        .tipoCuenta(TipoCuentaResponse.builder().id(2L).nombre("CORRIENTE").build())
                        .estadoCuenta(EstadoCuentaResponse.builder().id(1L).nombre("ACTIVO").build())
                        .saldo(new BigDecimal("2500.0"))
                        .build()
        );

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(cuentaClient.getCuentasByClienteId(clienteId)).thenReturn(cuentaResponses);
        List<CuentaResponse> cuentas = clienteService.obtenerClienteConCuentas(clienteId);
        assertAll(
                () -> assertNotNull(cuentas),
                () -> assertEquals(2, cuentas.size()),
                () -> assertEquals(cuentaResponses, cuentas)
        );
    }

    @Test
    @DisplayName("Obtener cliente con cuentas fallo feignException")
    void obtenerClienteConCuentas_fallFeignException() {
        Long clienteId = 1L;

        when(clienteRepository.existsById(clienteId)).thenReturn(true);
        when(cuentaClient.getCuentasByClienteId(clienteId))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThrows(ResourceNotFound.class, () ->
                clienteService.obtenerClienteConCuentas(clienteId)
        );
        verify(cuentaClient).getCuentasByClienteId(clienteId);
    }
    @Test
    @DisplayName("Fallback debe lanzar RuntimeException con mensaje correcto")
    void fallbackObtenerClienteConCuentas_shouldThrowRuntimeException() {
        Long clienteId = 1L;
        Throwable cause = new RuntimeException("Feign timeout");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.fallbackObtenerClienteConCuentas(clienteId, cause);
        });

        assertEquals("Servicio de cuentas no disponible. Intente más tarde.", exception.getMessage());
    }
    @Test
    @DisplayName("Debe devolver ClienteResponse al actualizar keycloakId correctamente")
    void getClienteResponse_success() {
        Long clienteId = 1L;
        String keycloakId = "kc-123";

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Juan")
                .email("juan@test.com")
                .build();

        Cliente updatedCliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Juan")
                .email("juan@test.com")
                .keycloakId(keycloakId)
                .build();

        ClienteResponse response = ClienteResponse.builder()
                .id(clienteId)
                .dni("12345678")
                .nombre("Juan")
                .email("juan@test.com")
                .keycloakId(keycloakId)
                .build();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(updatedCliente);
        when(clienteMapper.toDto(updatedCliente)).thenReturn(response);

        ClienteResponse result = clienteService.getClienteResponse(clienteId, keycloakId);

        assertNotNull(result);
        assertEquals(keycloakId, result.keycloakId());
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).save(cliente);
        verify(clienteMapper).toDto(updatedCliente);
    }
    @Test
    @DisplayName("Debe lanzar ResourceNotFound si el cliente no existe")
    void getClienteResponse_notFound() {
        Long clienteId = 99L;

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () ->
                clienteService.getClienteResponse(clienteId, "kc-xyz")
        );

        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository, never()).save(any());
        verify(clienteMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("cliente buscar por nombre containig success")
    void buscarClientesPorNombreContaining_success() {
        String nombreParte = "Fran";
        List<Cliente> clientes = List.of(
                Cliente.builder().id(1L).nombre("Frank Carpio").dni("12345678").email("frankcarpio@gmail.com").build(),
                Cliente.builder().id(2L).nombre("Franco Perez").dni("87654321").email("francoperez@gmail.com").build()
        );
        List<ClienteResponse> clienteResponses = List.of(
                ClienteResponse.builder().id(1L).nombre("Frank Carpio").dni("12345678").email("frankcarpio@gmail.com").build(),
                ClienteResponse.builder().id(2L).nombre("Franco Perez").dni("87654321").email("francoperez@gmail.com").build()
        );
        when(clienteRepository.findByNombreContaining(nombreParte)).thenReturn(clientes);
        when(clienteMapper.toDtoList(clientes)).thenReturn(clienteResponses);
        List<ClienteResponse> result = clienteService.buscarPorNombre(nombreParte);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(clienteResponses, result);
        verify(clienteRepository).findByNombreContaining(nombreParte);
        verify(clienteMapper).toDtoList(clientes);
    }
    @Test
    @DisplayName("obtener clientes por estado success")
    void obtenerClientesPorEstado_success() {
        Long estadoId = 1L;

        List<Cliente> clientes = List.of(
                Cliente.builder().id(1L).nombre("Frank Carpio").estadoCliente(EstadoCliente.builder().id(estadoId).estado("ACTIVO").build()).dni("12345678").email("frankcarpio@gmail.com").build(),
                Cliente.builder().id(2L).nombre("Franco Perez").estadoCliente(EstadoCliente.builder().id(estadoId).estado("ACTIVO").build()).dni("87654321").email("francoperez@gmail.com").build()
        );
        List<ClienteResponse> clienteResponses = List.of(
                ClienteResponse.builder().id(1L).nombre("Frank Carpio").estadoCliente(EstadoClienteResponse.builder().id(estadoId).estado("ACTIVO").build()).dni("12345678").email("frankcarpio@gmail.com").build(),
                ClienteResponse.builder().id(2L).nombre("Franco Perez").estadoCliente(EstadoClienteResponse.builder().id(estadoId).estado("ACTIVO").build()).dni("87654321").email("francoperez@gmail.com").build()
        );
        when(clienteRepository.findByEstado(estadoId)).thenReturn(clientes);
        when(clienteMapper.toDtoList(clientes)).thenReturn(clienteResponses);
        List<ClienteResponse> result = clienteService.obtenerClientesPorEstado(estadoId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(clienteResponses, result);
        verify(clienteRepository).findByEstado(estadoId);
        verify(clienteMapper).toDtoList(clientes);
    }
    @Test
    @DisplayName("obtener todos los clientes")
    void obtenerTodosLosClientes_success() {
        List<Cliente> clientes = List.of(
                Cliente.builder().id(1L).nombre("Frank Carpio").estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build()).dni("12345678").email("frankcarpio@gmail.com").build(),
                Cliente.builder().id(2L).nombre("Franco Perez").estadoCliente(EstadoCliente.builder().id(2L).estado("INACTIVO").build()).dni("87654321").email("francoperez@gmail.com").build()
        );
        List<ClienteResponse> clienteResponses = List.of(
                ClienteResponse.builder().id(1L).nombre("Frank Carpio").estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build()).dni("12345678").email("frankcarpio@gmail.com").build(),
                ClienteResponse.builder().id(2L).nombre("Franco Perez").estadoCliente(EstadoClienteResponse.builder().id(2L).estado("INACTIVO").build()).dni("87654321").email("francoperez@gmail.com").build()
        );
        when(clienteRepository.findAll()).thenReturn(clientes);
        when(clienteMapper.toDtoList(clientes)).thenReturn(clienteResponses);
        List<ClienteResponse> result = clienteService.obtenerTodosClientes();
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(clienteResponses, result)
        );
        verify(clienteRepository).findAll();
        verify(clienteMapper).toDtoList(clientes);
    }
    @Test
    @DisplayName("Crear cliente exitosamente")
    void crearCliente_success() {
        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoClienteId(1L)
                .build();
        Cliente clienteToSave = Cliente.builder()
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).build())
                .build();
        Cliente savedCliente = Cliente.builder()
                .id(1L)
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        ClienteResponse clienteResponse = ClienteResponse.builder()
                .id(1L)
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoCliente(EstadoClienteResponse.builder().id(1L).estado("ACTIVO").build())
                .build();
        when(clienteMapper.toEntity(clienteRequest)).thenReturn(clienteToSave);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);
        when(clienteMapper.toDto(savedCliente)).thenReturn(clienteResponse);
        ClienteResponse result = clienteService.crearCliente(clienteRequest);
        assertNotNull(result);
        assertEquals(clienteResponse, result);
        verify(clienteMapper).toEntity(clienteRequest);
        verify(clienteRepository).save(clienteToSave);
        verify(clienteMapper).toDto(savedCliente);
    }
    @Test
    @DisplayName("Debe lanzar BadRequest cuando el DNI ya existe al crear")
    void crearCliente_throwsExceptionWhenDniExists() {
        Cliente clienteExist = Cliente.builder()
                .id(2L)
                .dni("12345678")
                .nombre("Juan Garcia")
                .email("juangarcia@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoClienteId(1L)
                .build();
        when(clienteRepository.findByDni(clienteRequest.dni())).thenReturn(Optional.of(clienteExist));
        assertThrows(RuntimeException.class, () -> {
            clienteService.crearCliente(clienteRequest);
        });
        verify(clienteRepository).findByDni(clienteRequest.dni());
        verify(clienteRepository, never()).save(any());
    }
    @Test
    @DisplayName("Debe lanzar BadRequest cuando el email ya existe al crear")
    void crearCliente_throwsExceptionWhenEmailExists() {
        Cliente clienteExist = Cliente.builder()
                .id(2L)
                .dni("12345678")
                .nombre("Juan Garcia")
                .email("frankcarpio@gmail.com")
                .estadoCliente(EstadoCliente.builder().id(1L).estado("ACTIVO").build())
                .build();
        ClienteRequest clienteRequest = ClienteRequest.builder()
                .dni("23232323")
                .nombre("Frank Carpio")
                .email("frankcarpio@gmail.com")
                .estadoClienteId(1L)
                .build();
        when(clienteRepository.findByEmail(clienteRequest.email())).thenReturn(Optional.of(clienteExist));
        assertThrows(RuntimeException.class, () -> {
            clienteService.crearCliente(clienteRequest);
        });
        verify(clienteRepository).findByEmail(clienteRequest.email());
        verify(clienteRepository, never()).save(any());
    }
    @Test
    @DisplayName("Debe lanzar BadRequest si el keycloakId ya existe en otro cliente")
    void registerKeyCloakId_shouldThrowBadRequestWhenKeycloakIdExists() {
        Long clienteId = 1L;
        String keycloakId = "kc-123";

        Cliente existingCliente = Cliente.builder()
                .id(2L)
                .dni("12345678")
                .nombre("Juan")
                .email("juan@test.com")
                .keycloakId(keycloakId)
                .build();

        when(clienteRepository.findByKeycloakId(keycloakId))
                .thenReturn(Optional.of(existingCliente));

        assertThrows(BadRequest.class, () ->
                clienteService.registerKeyCloakId(clienteId, keycloakId)
        );

        verify(clienteRepository).findByKeycloakId(keycloakId);
        verify(clienteRepository, never()).findById(any());
        verify(clienteRepository, never()).save(any());
    }
    @Test
    @DisplayName("Debe registrar un nuevo keycloakId correctamente")
    void registerKeyCloakId_shouldRegisterSuccessfully() {
        Long clienteId = 1L;
        String keycloakId = "kc-999";

        Cliente cliente = Cliente.builder()
                .id(clienteId)
                .dni("87654321")
                .nombre("Franklin")
                .email("franklin@test.com")
                .build();

        Cliente updatedCliente = Cliente.builder()
                .id(clienteId)
                .dni("87654321")
                .nombre("Franklin")
                .email("franklin@test.com")
                .keycloakId(keycloakId)
                .build();

        ClienteResponse response = ClienteResponse.builder()
                .id(clienteId)
                .dni("87654321")
                .nombre("Franklin")
                .email("franklin@test.com")
                .keycloakId(keycloakId)
                .build();

        when(clienteRepository.findByKeycloakId(keycloakId))
                .thenReturn(Optional.empty()); // no existe ese keycloakId
        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(updatedCliente);
        when(clienteMapper.toDto(updatedCliente))
                .thenReturn(response);

        ClienteResponse result = clienteService.registerKeyCloakId(clienteId, keycloakId);

        assertNotNull(result);
        assertEquals(keycloakId, result.keycloakId());
        assertEquals(clienteId, result.id());

        verify(clienteRepository).findByKeycloakId(keycloakId);
        verify(clienteRepository).findById(clienteId);
        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteMapper).toDto(updatedCliente);
    }
}
