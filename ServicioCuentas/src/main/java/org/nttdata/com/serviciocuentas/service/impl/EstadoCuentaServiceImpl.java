package org.nttdata.com.serviciocuentas.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.EstadoCuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.EstadoCuenta;
import org.nttdata.com.serviciocuentas.repository.EstadoCuentaRepository;
import org.nttdata.com.serviciocuentas.service.EstadoCuentaService;
import org.nttdata.com.serviciocuentas.util.EstadoCuentaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoCuentaServiceImpl implements EstadoCuentaService {
    private final EstadoCuentaRepository estadoCuentaRepository;
    private final EstadoCuentaMapper estadoCuentaMapper;

    @Override
    public List<EstadoCuentaResponse> getAllEstadosCuenta() {
        return estadoCuentaMapper.toDtoList(estadoCuentaRepository.findAll());
    }

    @Override
    public EstadoCuentaResponse getEstadoCuentaById(Long id) {
        return estadoCuentaMapper.toDto(estadoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuenta no encontrado con id: " + id)
        ));
    }

    @Override
    public EstadoCuenta getEstadoCuentaEntityById(Long id) {
        return estadoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuenta no encontrado con id: " + id)
        );
    }

    @Override
    public EstadoCuentaResponse saveEstadoCuenta(EstadoCuentaRequest estadoCuentaRequest) {
        return estadoCuentaMapper.toDto(estadoCuentaRepository.save(estadoCuentaMapper.toEntity(estadoCuentaRequest)));
    }

    @Override
    public EstadoCuentaResponse updateEstadoCuenta(Long id, EstadoCuentaRequest estadoCuentaRequest) {
        EstadoCuenta estadoCuentaFound = estadoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuenta no encontrado con id: " + id)
        );
        estadoCuentaFound.setNombre(estadoCuentaRequest.nombre());
        return estadoCuentaMapper.toDto(estadoCuentaRepository.save(estadoCuentaFound));
    }

    @Override
    public void deleteEstadoCuenta(Long id) {
        EstadoCuenta estadoCuentaFound = estadoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuenta no encontrado con id: " + id)
        );
        estadoCuentaRepository.delete(estadoCuentaFound);
    }
}
