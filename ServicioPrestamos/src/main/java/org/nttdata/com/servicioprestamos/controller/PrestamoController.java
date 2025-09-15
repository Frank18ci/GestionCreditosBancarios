package org.nttdata.com.servicioprestamos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.dto.PrestamoRequest;
import org.nttdata.com.servicioprestamos.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prestamos")
@RequiredArgsConstructor
public class PrestamoController {
    private final PrestamoService prestamoService;

    @GetMapping
    public ResponseEntity<?> listarPrestamos() {
        return ResponseEntity.ok(prestamoService.getAllPrestamos());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPrestamoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.getPrestamoById(id));
    }
    @PostMapping
    public ResponseEntity<?> crearPrestamo(@Valid @RequestBody PrestamoRequest prestamo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.createPrestamo(prestamo));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPrestamo(@PathVariable Long id,@Valid @RequestBody PrestamoRequest prestamo) {
        return ResponseEntity.ok(prestamoService.updatePrestamo(id, prestamo));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPrestamo(@PathVariable Long id) {
        prestamoService.deletePrestamo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PostMapping("/aprobar/{id}")
    public ResponseEntity<?> aceptarPrestamo(@PathVariable Long id) {

        return ResponseEntity.ok(prestamoService.aceptarPrestamo(id));
    }
}

