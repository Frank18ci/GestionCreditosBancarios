package org.nttdata.com.servicioprestamos.service;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.repository.EstadoPrestamoRepository;
import org.nttdata.com.servicioprestamos.service.EstadoPrestamoService;
import org.nttdata.com.servicioprestamos.util.EstadoPrestamoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoPrestamoServiceImpl implements EstadoPrestamoService {
    private final EstadoPrestamoRepository estadoPrestamoRepository;
    private final EstadoPrestamoMapper estadoPrestamoMapper;


    @Override
    public List<EstadoPrestamoResponse> getAllEstadosPrestamo() {
        return estadoPrestamoMapper.toDtoList(estadoPrestamoRepository.findAll());
    }

    @Override
    public EstadoPrestamoResponse getEstadoPrestamoById(Long id) {
        return estadoPrestamoMapper.toDto(estadoPrestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de préstamo no encontrado con id: " + id)
        ));
    }

    @Override
    public EstadoPrestamo getEstadoPrestamoEntityById(Long id) {
        return estadoPrestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de préstamo no encontrado con id: " + id)
        );
    }

    @Override
    public EstadoPrestamoResponse createEstadoPrestamo(EstadoPrestamoRequest estadoPrestamoDto) {
        EstadoPrestamo estadoPrestamo = estadoPrestamoMapper.toEntity(estadoPrestamoDto);
        return estadoPrestamoMapper.toDto(estadoPrestamoRepository.save(estadoPrestamo));
    }

    @Override
    public EstadoPrestamoResponse updateEstadoPrestamo(Long id, EstadoPrestamoRequest estadoPrestamoDto) {
        EstadoPrestamo estadoPRestamoFound = estadoPrestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de préstamo no encontrado con id: " + id)
        );

        estadoPRestamoFound.setNombre(estadoPrestamoDto.nombre());

        return estadoPrestamoMapper.toDto(estadoPrestamoRepository.save(estadoPRestamoFound));
    }

    @Override
    public void deleteEstadoPrestamo(Long id) {
        EstadoPrestamo estadoPrestamoFound = estadoPrestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Estado de préstamo no encontrado con id: " + id)
        );
        estadoPrestamoRepository.delete(estadoPrestamoFound);
    }
}
