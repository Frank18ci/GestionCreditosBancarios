package org.nttdata.com.serviciocuentas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaRequest;
import org.nttdata.com.serviciocuentas.service.EstadoCuentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estado-cuentas")
@RequiredArgsConstructor
public class EstadoCuentaController {
    private final EstadoCuentaService estadoCuentaService;

    @GetMapping
    public ResponseEntity<?> getAllEstadoCuentas() {
        return ResponseEntity.ok(estadoCuentaService.getAllEstadosCuenta());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEstadoCuentaById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(estadoCuentaService.getEstadoCuentaById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createEstadoCuenta(@Valid @RequestBody EstadoCuentaRequest estadoCuentaRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estadoCuentaService.saveEstadoCuenta(estadoCuentaRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstadoCuenta(@PathVariable Long id, @Valid @RequestBody EstadoCuentaRequest estadoCuentaRequest) {
        return ResponseEntity.ok(estadoCuentaService.updateEstadoCuenta(id, estadoCuentaRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstadoCuenta(@PathVariable Long id) {
        estadoCuentaService.deleteEstadoCuenta(id);
        return ResponseEntity.noContent().build();
    }
}
