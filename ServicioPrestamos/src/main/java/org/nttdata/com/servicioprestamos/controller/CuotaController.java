package org.nttdata.com.servicioprestamos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.service.CuotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cuotas")
public class CuotaController {
    private final CuotaService cuotaService;

    @GetMapping
    public ResponseEntity<?> listarCuotas() {
        return ResponseEntity.ok(cuotaService.getAllCuotas());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCuotaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuotaService.getCuotaById(id));
    }
    @GetMapping("/prestamo/{prestamoId}")
    public ResponseEntity<?> obtenerCuotasPorPrestamoId(@PathVariable Long prestamoId) {
        return ResponseEntity.ok(cuotaService.getCuotasByPrestamoId(prestamoId));
    }
    @PostMapping
    public ResponseEntity<?> crearCuota(@Valid @RequestBody CuotaRequest cuotaRequest) {
        return ResponseEntity.ok(cuotaService.saveCuota(cuotaRequest));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCuota(@PathVariable Long id, @Valid @RequestBody CuotaRequest cuotaRequest) {
        return ResponseEntity.ok(cuotaService.updateCuota(id, cuotaRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCuota(@PathVariable Long id) {
        cuotaService.deleteCuota(id);
        return ResponseEntity.noContent().build();
    }
}
