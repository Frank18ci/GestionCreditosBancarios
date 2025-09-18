package org.nttdata.com.serviciotransacciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionRequest;
import org.nttdata.com.serviciotransacciones.dto.TipoTransaccionResponse;
import org.nttdata.com.serviciotransacciones.model.TipoTransaccion;
import org.nttdata.com.serviciotransacciones.repository.TipoTransaccionRepository;
import org.nttdata.com.serviciotransacciones.service.TipoTransaccionService;
import org.nttdata.com.serviciotransacciones.util.TipoTransaccionMapper;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoTransaccionServiceImpl implements TipoTransaccionService {
    private final TipoTransaccionRepository tipoTransaccionRepository;
    private final TipoTransaccionMapper tipoTransaccionMapper;


    @Override
    public List<TipoTransaccionResponse> getAllTipoTransacciones() {
        return tipoTransaccionMapper.toDtoList(tipoTransaccionRepository.findAll());
    }

    @Override
    public TipoTransaccionResponse getTipoTransaccionById(Long id) {
        return tipoTransaccionMapper.toDto(tipoTransaccionRepository.findById(id).orElseThrow(
                () -> new ResolutionException("Tipo de transaccion no encontrado con id: " + id)
        ));
    }

    @Override
    public TipoTransaccionResponse createTipoTransaccion(TipoTransaccionRequest tipoTransaccionRequest) {
        return tipoTransaccionMapper.toDto(
                tipoTransaccionRepository.save(
                        tipoTransaccionMapper.toEntity(tipoTransaccionRequest)
                )
        );
    }

    @Override
    public TipoTransaccionResponse updateTipoTransaccion(Long id, TipoTransaccionRequest tipoTransaccionRequest) {
        TipoTransaccion tipoTransaccionFound = tipoTransaccionRepository.findById(id).orElseThrow(
                () -> new ResolutionException("Tipo de transaccion no encontrado con id: " + id)
        );
        tipoTransaccionFound.setNombre(tipoTransaccionRequest.nombre());

        return tipoTransaccionMapper.toDto(tipoTransaccionRepository.save(tipoTransaccionFound));
    }

    @Override
    public void deleteTipoTransaccion(Long id) {
        TipoTransaccion tipoTransaccionFound = tipoTransaccionRepository.findById(id).orElseThrow(
                () -> new ResolutionException("Tipo de transaccion no encontrado con id: " + id)
        );
        tipoTransaccionRepository.delete(tipoTransaccionFound);
    }
}
