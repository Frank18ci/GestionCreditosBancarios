package org.nttdata.com.servicioprestamos.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.client.CuentaClient;
import org.nttdata.com.servicioprestamos.client.TransaccionClient;
import org.nttdata.com.servicioprestamos.client.dto.CuentaRequest;
import org.nttdata.com.servicioprestamos.client.dto.CuentaResponse;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.Cuota;
import org.nttdata.com.servicioprestamos.models.EstadoCuota;
import org.nttdata.com.servicioprestamos.repository.CuotaRepository;
import org.nttdata.com.servicioprestamos.service.CuotaService;
import org.nttdata.com.servicioprestamos.util.CuotaMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements CuotaService {
    private final CuotaRepository cuotaRepository;
    private final CuotaMapper cuotaMapper;
    private final CuentaClient cuentaClient;
    private final TransaccionClient transaccionClient;

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
    public CuotaResponse pagarCuota(Long id, Long cuentaId) {
        Cuota cuota = cuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Cuota no encontrada con id: " + id)
        );
        // verificar estado
        if(cuota.getEstadoCuota().getId() != 1L){
            throw new IllegalStateException("La cuota no se encuentra en estado PENDIENTE");
        }
        // verificar monto
        if (cuota.getMonto() == null || cuota.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Monto de cuota inválido");
        }
        // consultar cuenta
        CuentaResponse cuentaResp;
        try {
            cuentaResp = cuentaClient.getCuentaById(cuentaId);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo consultar la cuenta: " + ex.getMessage(), ex);
        }
        // verificar saldo
        if(cuentaResp.saldo().compareTo(cuota.getMonto()) < 0){
            throw new IllegalStateException("Saldo insuficiente en la cuenta para pagar la cuota");
        }
        //hoy
        LocalDate hoy = LocalDate.now();

            // registrar transacción de débito en el servicio de cuentas
            TransaccionRequest transaccion = TransaccionRequest.builder()
                .cuentaId(cuentaId)
                .monto(cuota.getMonto())
                // Suponiendo que el tipo 3L corresponde a "TRANSFERENCIA" o "PAGO DE CUOTA"
                .tipoTransaccionId(3L)
                .referencia("Pago cuota id:" + id)
                .fecha(Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        try {
            transaccionClient.crearTransaccion(transaccion);
        } catch (Exception ex) {
            throw new IllegalStateException("Error al registrar transacción en cuenta: " + ex.getMessage(), ex);
        }

        // Restar monto de la cuenta
        CuentaResponse cuentaResponse = cuentaClient.getCuentaById(cuentaId);

        CuentaRequest cuentaRequest = CuentaRequest.builder()
                .estadoCuentaId(cuentaResponse.estadoCuenta().id())
                .tipoCuentaId(cuentaResponse.tipoCuenta().id())
                .clienteId(cuentaResponse.clienteId())
                .saldo(cuentaResponse.saldo().subtract(transaccion.monto()))
                .build();

        cuentaClient.updateCuenta(cuentaId, cuentaRequest);

        // Actualizar estado de la cuota a PAGADA (suponiendo id 2L es PAGADA)
        cuota.setEstadoCuota(EstadoCuota.builder().id(2L).build());

        return cuotaMapper.toDto(cuotaRepository.save(cuota));
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
        cuotaFound.setNumero(cuotaRequest.numero());
        cuotaFound.setMonto(cuotaRequest.monto());
        cuotaFound.setFechaVencimiento(cuotaRequest.fechaVencimiento());
        cuotaFound.setMonto(cuotaRequest.monto());
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
