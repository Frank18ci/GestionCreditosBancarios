package org.nttdata.com.servicioprestamos.service;

import org.nttdata.com.servicioprestamos.dto.PrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.models.Prestamo;

import java.util.List;

public interface PrestamoService {
    List<PrestamoResponse> getAllPrestamos();
    PrestamoResponse getPrestamoById(Long id);
    Prestamo getPrestamoEntityById(Long id);
    PrestamoResponse createPrestamo(PrestamoRequest prestamoDto);
    PrestamoResponse updatePrestamo(Long id, PrestamoRequest prestamoDto);
    void deletePrestamo(Long id);
    List<PrestamoResponse> getPrestamosByClienteId(Long clienteId);
    PrestamoResponse aceptarPrestamo(Long id);
}
