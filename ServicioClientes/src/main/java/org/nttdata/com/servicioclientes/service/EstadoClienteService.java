package org.nttdata.com.servicioclientes.service;

import org.nttdata.com.servicioclientes.dto.EstadoClienteRequest;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;

import java.util.List;

public interface EstadoClienteService {
    List<EstadoClienteResponse> obtenerTodosLosEstados();
    EstadoClienteResponse obtenerEstadoPorId(Long id);
    EstadoClienteResponse crearEstado(EstadoClienteRequest estadoClienteRequest);
    EstadoClienteResponse actualizarEstado(Long id, EstadoClienteRequest estadoClienteRequest);
    void eliminarEstado(Long id);
}
