package org.nttdata.com.serviciocuentas.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciocuentas.dto.CuentaRequest;
import org.nttdata.com.serviciocuentas.dto.CuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.Cuenta;
import org.nttdata.com.serviciocuentas.repository.CuentaRepository;
import org.nttdata.com.serviciocuentas.service.CuentaService;
import org.nttdata.com.serviciocuentas.service.EstadoCuentaService;
import org.nttdata.com.serviciocuentas.util.CuentaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private static final Logger logger = LoggerFactory.getLogger(CuentaServiceImpl.class);

    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    private final EstadoCuentaService estadoCuentaService;

    @Override
    @Transactional
    public CuentaResponse crearCuenta(CuentaRequest request) {
        logger.info("Creando cuenta para cliente ID: {}", request.clienteId());

        try {
            if (request.clienteId() == null) {
                throw new RuntimeException("ID de cliente es requerido");
            }
            if (request.tipoCuentaId() == null || request.estadoCuentaId() == null) {
                throw new RuntimeException("Tipo y estado de cuenta son requeridos");
            }
            Cuenta cuenta = cuentaMapper.toEntity(request);
            Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
            logger.info("Cuenta creada exitosamente ID: {}", cuentaGuardada.getId());
            return cuentaMapper.toDto(cuentaGuardada);
        } catch (Exception e) {
            logger.error("Error al crear cuenta: {}", e.getMessage());
            throw new RuntimeException("Error al crear cuenta: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse obtenerCuentaPorId(Long id) {
        logger.info("Buscando cuenta ID: {}", id);
        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(id);
        if (cuentaOpt.isEmpty()) {
            logger.warn("Cuenta no encontrada ID: {}", id);
            throw new ResourceNotFound("Cuenta no encontrada con ID: " + id);
        }
        return cuentaMapper.toDto(cuentaOpt.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> obtenerCuentasPorCliente(Long idCliente) {
        logger.info("Buscando cuentas para cliente ID: {}", idCliente);
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(idCliente);
        logger.info("Encontradas {} cuentas para cliente ID: {}", cuentas.size(), idCliente);
        return cuentaMapper.toDtoList(cuentas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> obtenerTodasCuentas() {
        logger.info("Obteniendo todas las cuentas");
        List<Cuenta> cuentas = cuentaRepository.findAll();
        logger.info("Total de cuentas encontradas: {}", cuentas.size());
        return cuentaMapper.toDtoList(cuentas);
    }

    @Override
    @Transactional
    public CuentaResponse actualizarCuenta(Long id, CuentaRequest request) {
        Cuenta cuentaRequest = cuentaMapper.toEntity(request);
        logger.info("Actualizando cuenta ID: {}", id);

        try {
            Optional<Cuenta> cuentaOpt = cuentaRepository.findById(id);
            if (cuentaOpt.isEmpty()) {
                throw new RuntimeException("Cuenta no encontrada con ID: " + id);
            }
            Cuenta cuenta = cuentaOpt.get();
            if (request.tipoCuentaId() != null) {
                cuenta.setTipoCuenta(cuentaRequest.getTipoCuenta());
            }
            if (request.estadoCuentaId() != null) {
                cuenta.setEstadoCuenta(cuentaRequest.getEstadoCuenta());
            }
            if (request.saldo() != null) {
                cuenta.setSaldo(request.saldo());
            }
            Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
            logger.info("Cuenta actualizada exitosamente ID: {}", id);
            return cuentaMapper.toDto(cuentaActualizada);
        } catch (Exception e) {
            logger.error("Error al actualizar cuenta ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar cuenta: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminarCuenta(Long id) {
        logger.info("Eliminando cuenta ID: {}", id);

        if (!cuentaRepository.existsById(id)) {
            logger.error("Cuenta no encontrada con ID: {}", id);
            throw new ResourceNotFound("Cuenta no encontrada con ID: " + id);
        }
        cuentaRepository.deleteById(id);
        logger.info("Cuenta eliminada exitosamente ID: {}", id);
    }

    @Override
    @Transactional
    public CuentaResponse actualizarSaldo(Long id, BigDecimal nuevoSaldo) {
        logger.info("Actualizando saldo cuenta ID: {} - Nuevo saldo: {}", id, nuevoSaldo);

        try {
            Optional<Cuenta> cuentaOpt = cuentaRepository.findById(id);
            if (cuentaOpt.isEmpty()) {
                throw new RuntimeException("Cuenta no encontrada con ID: " + id);
            }
            Cuenta cuenta = cuentaOpt.get();
            cuenta.setSaldo(nuevoSaldo);
            Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
            logger.info("Saldo actualizado exitosamente para cuenta ID: {}", id);
            return cuentaMapper.toDto(cuentaActualizada);
        } catch (Exception e) {
            logger.error("Error al actualizar saldo cuenta ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar saldo: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CuentaResponse cambiarEstadoCuenta(Long id, Long estadoCuentaId) {
        logger.info("Cambiando estado cuenta ID: {} - Nuevo estado: {}", id, estadoCuentaId);

        try {
            Optional<Cuenta> cuentaOpt = cuentaRepository.findById(id);
            if (cuentaOpt.isEmpty()) {
                throw new RuntimeException("Cuenta no encontrada con ID: " + id);
            }
            Cuenta cuenta = cuentaOpt.get();
            cuenta.setEstadoCuenta(estadoCuentaService.getEstadoCuentaEntityById(estadoCuentaId));
            Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
            logger.info("Estado actualizado exitosamente para cuenta ID: {}", id);
            return cuentaMapper.toDto(cuentaActualizada);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de cuenta inválido: " + estadoCuentaId);
        } catch (Exception e) {
            logger.error("Error al cambiar estado cuenta ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al cambiar estado: " + e.getMessage());
        }
    }
}
