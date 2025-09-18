package org.nttdata.com.serviciocuentas.service.impl;

import lombok.RequiredArgsConstructor;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaRequest;
import org.nttdata.com.serviciocuentas.dto.TipoCuentaResponse;
import org.nttdata.com.serviciocuentas.exception.ResourceNotFound;
import org.nttdata.com.serviciocuentas.model.TipoCuenta;
import org.nttdata.com.serviciocuentas.repository.TipoCuentaRepository;
import org.nttdata.com.serviciocuentas.service.TipoCuentaService;
import org.nttdata.com.serviciocuentas.util.TipoCuentaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoCuentaServiceImpl implements TipoCuentaService {
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TipoCuentaMapper tipoCuentaMapper;

    @Override
    public List<TipoCuentaResponse> getAllTiposCuenta() {
        return tipoCuentaMapper.toDtoList(tipoCuentaRepository.findAll());
    }

    @Override
    public TipoCuentaResponse getTipoCuentaById(Long id) {
        return tipoCuentaMapper.toDto(tipoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Tipo de cuenta no encontrado con id: " + id)
        ));
    }

    @Override
    public TipoCuentaResponse createTipoCuenta(TipoCuentaRequest tipoCuentaRequest) {
        return tipoCuentaMapper.toDto(tipoCuentaRepository.save(tipoCuentaMapper.toEntity(tipoCuentaRequest)));
    }

    @Override
    public TipoCuentaResponse updateTipoCuenta(Long id, TipoCuentaRequest tipoCuentaRequest) {
        TipoCuenta tipoCuentaFound = tipoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Tipo de cuenta no encontrado con id: " + id)
        );
        tipoCuentaFound.setNombre(tipoCuentaRequest.nombre());
        return tipoCuentaMapper.toDto(tipoCuentaRepository.save(tipoCuentaFound));
    }

    @Override
    public void deleteTipoCuenta(Long id) {
        TipoCuenta tipoCuentaFound = tipoCuentaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Tipo de cuenta no encontrado con id: " + id)
        );
        tipoCuentaRepository.delete(tipoCuentaFound);
    }
}
