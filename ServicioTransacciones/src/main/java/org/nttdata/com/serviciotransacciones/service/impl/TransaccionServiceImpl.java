package org.nttdata.com.serviciotransacciones.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciotransacciones.client.CuentaClient;
import org.nttdata.com.serviciotransacciones.client.dto.CuentaResponse;
import org.nttdata.com.serviciotransacciones.dto.TransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TransaccionResponse;
import org.nttdata.com.serviciotransacciones.exception.ResourceNotFound;
import org.nttdata.com.serviciotransacciones.model.Transaccion;
import org.nttdata.com.serviciotransacciones.repository.TransaccionRepository;
import org.nttdata.com.serviciotransacciones.service.TransaccionService;
import org.nttdata.com.serviciotransacciones.util.TransaccionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionServiceImpl implements TransaccionService {
    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;
    private final CuentaClient cuentaClient;

    private static final String CUENTA_SERVICE_CB = "cuentaService";

    @Override
    public List<TransaccionResponse> getAllTransacciones() {
        return transaccionMapper.toDtoList(transaccionRepository.findAll());
    }

    @Override
    public TransaccionResponse getTransaccionById(Long id) {
        return transaccionMapper.toDto(transaccionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Transaccion no encontrada con id: " + id)
        ));
    }

    @Override
    public TransaccionResponse createTransaccion(TransaccionRequest transaccionRequest) {

        cuentaClient.getCuentaById(transaccionRequest.cuentaId()); // Verifica que la cuenta exista

        return transaccionMapper.toDto(transaccionRepository.save(
                transaccionMapper.toEntity(transaccionRequest)
        ));
    }

    @Override
    public TransaccionResponse updateTransaccion(Long id, TransaccionRequest transaccionRequest) {
        Transaccion transaccionFound = transaccionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Transaccion no encontrada con id: " + id)
        );

        cuentaClient.getCuentaById(transaccionRequest.cuentaId()); // Verifica que la cuenta exista

        transaccionFound.setCuentaId(transaccionRequest.cuentaId());
        transaccionFound.setTipoTransaccion(transaccionMapper.toEntity(transaccionRequest).getTipoTransaccion());
        transaccionFound.setMonto(transaccionRequest.monto());
        transaccionFound.setFecha(transaccionRequest.fecha());
        transaccionFound.setReferencia(transaccionRequest.referencia());

        return transaccionMapper.toDto(transaccionRepository.save(transaccionFound));
    }

    @Override
    public void deleteTransaccion(Long id) {
        Transaccion transaccionFound = transaccionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Transaccion no encontrada con id: " + id)
        );
        transaccionRepository.delete(transaccionFound);
    }

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = CUENTA_SERVICE_CB, fallbackMethod = "getTransaccionesByCuentaIdFallBack")
    public List<TransaccionResponse> getTransaccionesByCuentaId(Long cuentaId) {
        CuentaResponse cuentaResponse;
        try{
            cuentaResponse = cuentaClient.getCuentaById(cuentaId);
            if(cuentaResponse == null || cuentaResponse.id() == null){
                throw new ResourceNotFound("La cuenta con id: " + cuentaId + " no existe");
            }
        } catch (Exception ex){
            throw new ResourceNotFound("La cuenta con id: " + cuentaId + " no existe");
        }
        List<Transaccion> transacciones = transaccionRepository.findByCuentaId(cuentaId);
        return transaccionMapper.toDtoList(transacciones);
    }

    public List<TransaccionResponse> getTransaccionesByCuentaIdFallBack(Long cuentaId, Throwable ex) {
        throw new ResourceNotFound("El servicio de cuentas no está disponible. Intente más tarde.");
    }
}
