package org.nttdata.com.servicioclientes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.service.ClienteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<?> crearCliente(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.crearCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerCliente(@PathVariable Long id) {
        ClienteResponse response = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> obtenerTodosClientes() {
        List<ClienteResponse> responses = clienteService.obtenerTodosClientes();
        return ResponseEntity.ok(responses);
    }
    // Inyectar el puerto del servidor para demostrar el balanceo de carga
    @Value("${server.port}")
    private String port;
    @GetMapping("/{idCliente}/cuentas")
    public ResponseEntity<?> obtenerClienteConCuentas(@PathVariable Long idCliente) {
        System.out.println("Atendiendo solicitud desde instancia con el puerto: " + port);
        return ResponseEntity.ok(clienteService.obtenerClienteConCuentas(idCliente));
    }

    @GetMapping("/estado/{estadoId}")
    public ResponseEntity<List<ClienteResponse>> obtenerClientesPorEstado(
            @PathVariable Long estadoId) {
        List<ClienteResponse> responses = clienteService.obtenerClientesPorEstado(estadoId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable Long id,@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.actualizarCliente(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/existe/{id}")
    public ResponseEntity<Boolean> existeCliente(@PathVariable Long id) {
        boolean existe = clienteService.existeCliente(id);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponse>> buscarPorNombre(
            @RequestParam String nombre) {
        List<ClienteResponse> responses = clienteService.buscarPorNombre(nombre);
        return ResponseEntity.ok(responses);
    }

    //Funcionalidad de keycloak
    @PostMapping("/register-keycloak/{id}")
    public ResponseEntity<?> registerUser(@PathVariable Long id, JwtAuthenticationToken auth) {
        String keycloakId = auth.getToken().getSubject();

        return ResponseEntity.status(HttpStatus.OK).body(clienteService.registerKeyCloakId(id, keycloakId));
    }
    @PostMapping("/update-keycloak/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, JwtAuthenticationToken auth) {
        String keycloakId = auth.getToken().getSubject();

        return ResponseEntity.status(HttpStatus.OK).body(clienteService.updateKeyCloakId(id, keycloakId));
    }
    @GetMapping("/mis-roles")
    public String misRoles(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
    }
    @GetMapping("/claims")
    public Map<String, Object> verClaims(JwtAuthenticationToken authentication) {
        return authentication.getToken().getClaims();
    }
}
