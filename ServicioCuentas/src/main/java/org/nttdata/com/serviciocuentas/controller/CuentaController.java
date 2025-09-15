package org.nttdata.com.serviciocuentas.controller;

import jakarta.validation.Valid;
import org.nttdata.com.serviciocuentas.dto.CuentaRequest;
import org.nttdata.com.serviciocuentas.dto.CuentaResponse;
import org.nttdata.com.serviciocuentas.service.CuentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/cuentas")
public class CuentaController {

    private static final Logger logger = LoggerFactory.getLogger(CuentaController.class);

    @Autowired
    private CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<?> crearCuenta(@Valid @RequestBody CuentaRequest request) {
        logger.info("POST /cuentas - Crear nueva cuenta");

        try {
            CuentaResponse response = cuentaService.crearCuenta(request);
            logger.info("Cuenta creada exitosamente ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error en POST /cuentas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCuenta(@PathVariable Long id) {
        logger.info("GET /cuentas/{} - Obtener cuenta", id);

        try {
            CuentaResponse response = cuentaService.obtenerCuentaPorId(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en GET /cuentas/{}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // Inyectar el puerto del servidor para demostrar el balanceo de carga
    @Value("${server.port}")
    private String port;
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> obtenerCuentasPorCliente(@PathVariable Long idCliente) {
        System.out.println("Atendiendo solicitud desde instancia con el puerto: " + port);
        logger.info("GET /cuentas/cliente/{} - Obtener cuentas por cliente", idCliente);

        List<CuentaResponse> responses = cuentaService.obtenerCuentasPorCliente(idCliente);
        return ResponseEntity.ok(responses);

    }

    @GetMapping
    public ResponseEntity<?> obtenerTodasCuentas() {
        logger.info("GET /cuentas - Obtener todas las cuentas");

        try {
            List<CuentaResponse> responses = cuentaService.obtenerTodasCuentas();
            return ResponseEntity.ok(responses);

        } catch (Exception e) {
            logger.error("Error en GET /cuentas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCuenta(@PathVariable Long id,@Valid @RequestBody CuentaRequest request) {
        logger.info("PUT /cuentas/{} - Actualizar cuenta", id);

        try {
            CuentaResponse response = cuentaService.actualizarCuenta(id, request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en PUT /cuentas/{}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCuenta(@PathVariable Long id) {
        logger.info("DELETE /cuentas/{} - Eliminar cuenta", id);

        try {
            cuentaService.eliminarCuenta(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            logger.error("Error en DELETE /cuentas/{}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/saldo")
    public ResponseEntity<?> actualizarSaldo(@PathVariable Long id, @RequestParam BigDecimal saldo) {
        logger.info("PATCH /cuentas/{}/saldo - Actualizar saldo: {}", id, saldo);

        try {
            CuentaResponse response = cuentaService.actualizarSaldo(id, saldo);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en PATCH /cuentas/{}/saldo: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoCuenta(@PathVariable Long id, @RequestParam Long estadoCuentaId) {
        logger.info("PATCH /cuentas/{}/estado - Cambiar estado: {}", id, estadoCuentaId);

        try {
            CuentaResponse response = cuentaService.cambiarEstadoCuenta(id, estadoCuentaId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en PATCH /cuentas/{}/estado: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("GET /cuentas/health - Health check");
        return ResponseEntity.ok("Servicio de Cuentas está funcionando correctamente");
    }
}