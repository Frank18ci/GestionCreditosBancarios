package org.nttdata.com.servicioprestamos.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaRequest;
import org.nttdata.com.servicioprestamos.dto.EstadoCuotaResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoCuota;
import org.nttdata.com.servicioprestamos.repository.EstadoCuotaRepository;
import org.nttdata.com.servicioprestamos.service.EstadoCuotaService;
import org.nttdata.com.servicioprestamos.util.EstadoCuotaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoCuotaServiceImpl implements EstadoCuotaService {
    private final EstadoCuotaRepository estadoCuotaRepository;
    private final EstadoCuotaMapper estadoCuotaMapper;

    @Override
    public List<EstadoCuotaResponse> getAllEstadosCuota() {
        return estadoCuotaMapper.toDtoList(estadoCuotaRepository.findAll());
    }

    @Override
    public EstadoCuotaResponse getEstadoCuotaById(Long id) {
        return estadoCuotaMapper.toDto(estadoCuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuota no encontrado con id: " + id)
        ));
    }

    @Override
    public EstadoCuota getEstadoCuotaEntityById(Long id) {
        return estadoCuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuota no encontrado con id: " + id)
        );
    }

    @Override
    public EstadoCuotaResponse saveEstadoCuota(EstadoCuotaRequest estadoCuotaRequest) {
        return estadoCuotaMapper.toDto(estadoCuotaRepository.save(estadoCuotaMapper.toEntity(estadoCuotaRequest)));
    }

    @Override
    public EstadoCuotaResponse updateEstadoCuota(Long id, EstadoCuotaRequest estadoCuotaRequest) {
        EstadoCuota estadoCuotaFound = estadoCuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuota no encontrado con id: " + id)
        );
        estadoCuotaFound.setNombre(estadoCuotaRequest.nombre());

        return estadoCuotaMapper.toDto(estadoCuotaRepository.save(estadoCuotaFound));
    }

    @Override
    public void deleteEstadoCuota(Long id) {
        EstadoCuota estadoCuotaFound = estadoCuotaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cuota no encontrado con id: " + id)
        );
        estadoCuotaRepository.delete(estadoCuotaFound);
    }
}
