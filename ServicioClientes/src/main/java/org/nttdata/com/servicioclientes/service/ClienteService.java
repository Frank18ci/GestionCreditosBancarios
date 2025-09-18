package org.nttdata.com.servicioclientes.service;

import org.nttdata.com.servicioclientes.client.dto.CuentaResponse;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;

import java.util.List;

public interface ClienteService {
    ClienteResponse crearCliente(ClienteRequest request);
    ClienteResponse obtenerClientePorId(Long id);
    List<ClienteResponse> obtenerTodosClientes();
    List<ClienteResponse> obtenerClientesPorEstado(Long estadoId);
    List<ClienteResponse> buscarPorNombre(String nombre);
    ClienteResponse actualizarCliente(Long id, ClienteRequest request);
    void eliminarCliente(Long id);
    boolean existeCliente(Long id);

    List<CuentaResponse> obtenerClienteConCuentas(Long idCliente);

    ClienteResponse registerKeyCloakId(Long id, String keycloakId);

    ClienteResponse updateKeyCloakId(Long id, String keycloakId);
}
