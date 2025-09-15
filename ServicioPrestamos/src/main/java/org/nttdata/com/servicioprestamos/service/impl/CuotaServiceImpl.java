package org.nttdata.com.servicioprestamos.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciocuentas.dto.CuentaResponse;
import org.nttdata.com.servicioprestamos.client.CuentaClient;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.Cuota;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.nttdata.com.servicioprestamos.repository.CuotaRepository;
import org.nttdata.com.servicioprestamos.service.CuotaService;
import org.nttdata.com.servicioprestamos.util.CuotaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import jakarta.ws.rs.BadRequestException;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements CuotaService {
    private final CuotaRepository cuotaRepository;
    private final CuentaClient cuentaClient;
    private final CuotaMapper cuotaMapper;

    @Override
    public List<CuotaResponse> getAllCuotas() {
        return cuotaMapper.toDtoList(cuotaRepository.findAll());
    }

    @Override
    public CuotaResponse getCuotaById(Long id) {
        return cuotaMapper.toDto(cuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Cuota no encontrada con id: " + id)
        ));
    }

    @Override
    @Transactional
    public CuotaResponse pagarCuota(Long id, Long cuentaId) {
        Cuota cuota = cuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Cuota no encontrada con id: " + id)
        );

        // verificar estado
        try {
            Method getEstado = cuota.getClass().getMethod("getEstadoCuota");
            Object estadoVal = getEstado.invoke(cuota);
            if (estadoVal != null && "PAGADA".equalsIgnoreCase(estadoVal.toString())) {
                throw new IllegalStateException("La cuota ya se encuentra pagada");
            }
        } catch (NoSuchMethodException nsme) {
            try {
                Field f = cuota.getClass().getDeclaredField("estadoCuota");
                f.setAccessible(true);
                Object estadoVal = f.get(cuota);
                if (estadoVal != null && "PAGADA".equalsIgnoreCase(estadoVal.toString())) {
                    throw new IllegalStateException("La cuota ya se encuentra pagada");
                }
            } catch (Exception ignored) { }
        } catch (Exception e) {
            throw new IllegalStateException("Error al verificar estado de cuota: " + e.getMessage(), e);
        }

        // conversión segura del monto
        BigDecimal monto;
        try {
            Object montoObj = cuota.getMonto();
            if (montoObj == null) {
                monto = BigDecimal.ZERO;
            } else if (montoObj instanceof BigDecimal) {
                monto = (BigDecimal) montoObj;
            } else if (montoObj instanceof Double) {
                monto = BigDecimal.valueOf((Double) montoObj);
            } else if (montoObj instanceof Number) {
                monto = BigDecimal.valueOf(((Number) montoObj).doubleValue());
            } else {
                monto = new BigDecimal(montoObj.toString());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo leer el monto de la cuota", ex);
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Monto de cuota inválido");
        }

        // obtener cuenta y verificar saldo
        CuentaResponse cuentaResp;
        try {
            cuentaResp = cuentaClient.getCuentaById(cuentaId);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo consultar la cuenta: " + ex.getMessage(), ex);
        }

        BigDecimal saldoCuenta = cuentaResp != null ? cuentaResp.getSaldo() : BigDecimal.ZERO;
        if (saldoCuenta == null || saldoCuenta.compareTo(monto) < 0) {
            throw new BadRequestException("Saldo insuficiente para pagar la cuota");
        }

        // registrar transacción de débito en el servicio de cuentas
        TransaccionRequest transaccion = new TransaccionRequest(monto, "DEBITO", "Pago cuota id:" + id);
        try {
            cuentaClient.registrarTransaccion(cuentaId, transaccion);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al registrar transacción en cuenta: " + ex.getMessage(), ex);
        }

        // marcar cuota como PAGADA
        try {
            // seteo directo del estado a PAGADA
            try {
                Field estadoField = cuota.getClass().getDeclaredField("estadoCuota");
                estadoField.setAccessible(true);
                Class<?> tipo = estadoField.getType();
                if (Enum.class.isAssignableFrom(tipo)) {
                    @SuppressWarnings("unchecked")
                    Class<Enum> enumClass = (Class<Enum>) tipo;
                    Enum valor = Enum.valueOf(enumClass, "PAGADA");
                    estadoField.set(cuota, valor);
                } else if (tipo.equals(String.class)) {
                    estadoField.set(cuota, "PAGADA");
                }
            } catch (NoSuchFieldException nsf) {
                try {
                    Method setEstado = cuota.getClass().getMethod("setEstadoCuota", String.class);
                    setEstado.invoke(cuota, "PAGADA");
                } catch (Exception ignored) {}
            }

            try {
                Method setFechaPago = cuota.getClass().getMethod("setFechaPago", LocalDate.class);
                setFechaPago.invoke(cuota, LocalDate.now());
            } catch (NoSuchMethodException ignored) { }

            // actualizar cuota
            Cuota actualizado = cuotaRepository.save(cuota);

            // actualizar estado del préstamo si todas las cuotas están pagadas
            Prestamo prestamo = actualizado.getPrestamo();
            if (prestamo != null) {
                boolean todasPagadas = false;
                try {
                    List<Cuota> cuotasPrestamo = prestamo.getCuotas();
                    if (cuotasPrestamo != null) {
                        todasPagadas = cuotasPrestamo.stream().allMatch(c -> {
                            Object est = c.getEstadoCuota();
                            return est != null && "PAGADA".equalsIgnoreCase(est.toString());
                        });
                    }
                } catch (Exception ignored) { }

                if (todasPagadas) {
                    try {
                        // intentar setear estado del préstamo a FINALIZADO
                        try {
                            Field estadoPrest = prestamo.getClass().getDeclaredField("estado");
                            estadoPrest.setAccessible(true);
                            Class<?> tipo = estadoPrest.getType();
                            if (Enum.class.isAssignableFrom(tipo)) {
                                @SuppressWarnings("unchecked")
                                Class<Enum> enumClass = (Class<Enum>) tipo;
                                Enum valor = Enum.valueOf(enumClass, "FINALIZADO");
                                estadoPrest.set(prestamo, valor);
                            } else if (tipo.equals(String.class)) {
                                estadoPrest.set(prestamo, "FINALIZADO");
                            }
                        } catch (NoSuchFieldException nsf) {
                            try {
                                Method setEstadoPrest = prestamo.getClass().getMethod("setEstado", String.class);
                                setEstadoPrest.invoke(prestamo, "FINALIZADO");
                            } catch (Exception ignored) {}
                        }

                    } catch (Exception ignored) {}
                }
            }

            return cuotaMapper.toDto(actualizado);
        } catch (Exception persistEx) {

           throw new IllegalStateException("Error al actualizar cuota después del débito", persistEx);
        }
    }

    @Override
    public CuotaResponse saveCuota(CuotaRequest cuotaRequest) {
        return cuotaMapper.toDto(cuotaRepository.save(cuotaMapper.toEntity(cuotaRequest)));
    }

    @Override
    public CuotaResponse updateCuota(Long id, CuotaRequest cuotaRequest) {
        Cuota cuotaEntityRequest = cuotaMapper.toEntity(cuotaRequest);

        Cuota cuotaFound = cuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Cuota no encontrada con id: " + id)
        );
        cuotaFound.setPrestamo(cuotaEntityRequest.getPrestamo());
        cuotaFound.setNumero(cuotaRequest.getNumero());
        cuotaFound.setMonto(cuotaRequest.getMonto());
        cuotaFound.setFechaVencimiento(cuotaRequest.getFechaVencimiento());
        cuotaFound.setMonto(cuotaRequest.getMonto());
        cuotaFound.setEstadoCuota(cuotaEntityRequest.getEstadoCuota());

        return cuotaMapper.toDto(cuotaRepository.save(cuotaFound));
    }

    @Override
    public void deleteCuota(Long id) {
        Cuota cuotaFound = cuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Cuota no encontrada con id: " + id)
        );
        cuotaRepository.delete(cuotaFound);

    }

    @Override
    public List<CuotaResponse> getCuotasByPrestamoId(Long prestamoId) {
        return cuotaMapper.toDtoList(cuotaRepository.findByPrestamoId(prestamoId));
    }
}
