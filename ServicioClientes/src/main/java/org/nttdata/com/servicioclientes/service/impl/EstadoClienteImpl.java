package org.nttdata.com.servicioclientes.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioclientes.dto.EstadoClienteRequest;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.model.EstadoCliente;
import org.nttdata.com.servicioclientes.repository.EstadoClienteRepository;
import org.nttdata.com.servicioclientes.service.EstadoClienteService;
import org.nttdata.com.servicioclientes.util.EstadoClienteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoClienteImpl implements EstadoClienteService {
    private final EstadoClienteRepository estadoClienteRepository;
    private final EstadoClienteMapper estadoClienteMapper;

    @Override
    public List<EstadoClienteResponse> obtenerTodosLosEstados() {
        return estadoClienteMapper.toDtoList(estadoClienteRepository.findAll());
    }

    @Override
    public EstadoClienteResponse obtenerEstadoPorId(Long id) {
        return estadoClienteMapper.toDto(estadoClienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cliente no encontrado con id: " + id)
        ));
    }

    @Override
    public EstadoClienteResponse crearEstado(EstadoClienteRequest estadoClienteRequest) {
        return estadoClienteMapper.toDto(estadoClienteRepository.save(estadoClienteMapper.toEntity(estadoClienteRequest)));
    }

    @Override
    public EstadoClienteResponse actualizarEstado(Long id, EstadoClienteRequest estadoClienteRequest) {
        EstadoCliente estadoClienteFound = estadoClienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cliente no encontrado con id: " + id)
        );
        estadoClienteFound.setEstado(estadoClienteRequest.estado());

        return estadoClienteMapper.toDto(estadoClienteRepository.save(estadoClienteFound));
    }

    @Override
    public void eliminarEstado(Long id) {
        EstadoCliente estadoClienteFound = estadoClienteRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de cliente no encontrado con id: " + id)
        );
        estadoClienteRepository.delete(estadoClienteFound);
    }
}
