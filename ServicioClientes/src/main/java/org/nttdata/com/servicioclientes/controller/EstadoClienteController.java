package org.nttdata.com.servicioclientes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioclientes.dto.EstadoClienteRequest;
import org.nttdata.com.servicioclientes.service.EstadoClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/estado-clientes")
public class EstadoClienteController {
    private final EstadoClienteService estadoClienteService;

    @GetMapping
    public ResponseEntity<?> getAllEstadoClientes() {
        return ResponseEntity.ok(estadoClienteService.obtenerTodosLosEstados());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEstadoClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(estadoClienteService.obtenerEstadoPorId(id));
    }
    @PostMapping
    public ResponseEntity<?> createEstadoCliente(@Valid @RequestBody EstadoClienteRequest estadoClienteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estadoClienteService.crearEstado(estadoClienteRequest));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstadoCliente(@PathVariable Long id, @Valid @RequestBody EstadoClienteRequest estadoClienteRequest) {
        return ResponseEntity.ok(estadoClienteService.actualizarEstado(id, estadoClienteRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstadoCliente(@PathVariable Long id) {
        estadoClienteService.eliminarEstado(id);
        return ResponseEntity.noContent().build();
    }
}
