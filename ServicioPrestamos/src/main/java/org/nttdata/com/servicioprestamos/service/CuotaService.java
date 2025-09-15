package org.nttdata.com.servicioprestamos.service;

import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaResponse;

import java.util.List;

public interface CuotaService {
    List<CuotaResponse> getAllCuotas();
    CuotaResponse getCuotaById(Long id);
    CuotaResponse pagarCuota(Long id, Long cuentaId);
    CuotaResponse saveCuota(CuotaRequest cuotaRequest);
    CuotaResponse updateCuota(Long id, CuotaRequest cuotaRequest);
    void deleteCuota(Long id);

    List<CuotaResponse> getCuotasByPrestamoId(Long prestamoId);
}
