package org.nttdata.com.servicioclientes.service.impl;


import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioclientes.client.CuentaClient;
import org.nttdata.com.servicioclientes.client.dto.CuentaResponse;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.model.Cliente;
import org.nttdata.com.servicioclientes.repository.ClienteRepository;
import org.nttdata.com.servicioclientes.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository clienteRepository;
    private final CuentaClient cuentaClient;

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private ClienteResponse mapToResponse(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setId(cliente.getId());
        response.setNombre(cliente.getNombre());
        response.setDni(cliente.getDni());
        response.setEmail(cliente.getEmail());
        response.setEstado(cliente.getEstado());
        return response;
    }

    private Cliente mapToEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setDni(request.getDni());
        cliente.setEmail(request.getEmail());
        cliente.setEstado(request.getEstado());
        return cliente;
    }

    @Override
    @Transactional
    public ClienteResponse crearCliente(ClienteRequest request) {
        logger.info("Intentando crear cliente: {}", request.getDni());

        try {
            // Validar DNI único
            if (clienteRepository.findByDni(request.getDni()).isPresent()) {
                logger.warn("DNI ya registrado: {}", request.getDni());
                throw new RuntimeException("DNI ya registrado: " + request.getDni());
            }

            // Validar Email único
            if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Email ya registrado: {}", request.getEmail());
                throw new RuntimeException("Email ya registrado: " + request.getEmail());
            }

            Cliente cliente = mapToEntity(request);
            Cliente clienteGuardado = clienteRepository.save(cliente);
            logger.info("Cliente creado exitosamente: {}", clienteGuardado.getId());

            return mapToResponse(clienteGuardado);

        } catch (DataIntegrityViolationException e) {
            logger.error("Error de integridad de datos: {}", e.getMessage());
            throw new RuntimeException("Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al crear cliente: {}", e.getMessage());
            throw new RuntimeException("Error al crear cliente: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(Long id) {
        logger.info("Buscando cliente por ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Cliente no encontrado con ID: {}", id);
                    return new ResourceNotFound("Cliente no encontrado con ID: " + id);
                });

        logger.info("Cliente encontrado: {}", cliente.getNombre());
        return mapToResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerTodosClientes() {
        logger.info("Obteniendo todos los clientes");

        List<Cliente> clientes = clienteRepository.findAll();
        logger.info("Encontrados {} clientes", clientes.size());

        return clientes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> obtenerClientesPorEstado(String estado) {
        logger.info("Buscando clientes por estado: {}", estado);

        List<Cliente> clientes = clienteRepository.findByEstado(estado);
        logger.info("Encontrados {} clientes con estado {}", clientes.size(), estado);

        return clientes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> buscarPorNombre(String nombre) {
        logger.info("Buscando clientes por nombre: {}", nombre);

        List<Cliente> clientes = clienteRepository.findByNombreContaining(nombre);
        logger.info("Encontrados {} clientes con nombre que contiene '{}'", clientes.size(), nombre);

        return clientes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ClienteResponse actualizarCliente(Long id, ClienteRequest request) {
        logger.info("Actualizando cliente ID: {}", id);

        try {
            Cliente cliente = clienteRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Cliente no encontrado para actualizar: {}", id);
                        return new RuntimeException("Cliente no encontrado con ID: " + id);
                    });

            // Validar que el DNI
            if (!cliente.getDni().equals(request.getDni())) {
                if (clienteRepository.findByDni(request.getDni()).isPresent()) {
                    logger.warn("DNI ya registrado por otro cliente: {}", request.getDni());
                    throw new RuntimeException("DNI ya registrado por otro cliente: " + request.getDni());
                }
            }

            // Validar que el Email no esté usado por otro cliente
            if (!cliente.getEmail().equals(request.getEmail())) {
                if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
                    logger.warn("Email ya registrado por otro cliente: {}", request.getEmail());
                    throw new RuntimeException("Email ya registrado por otro cliente: " + request.getEmail());
                }
            }

            // Actualizar datos
            cliente.setNombre(request.getNombre());
            cliente.setDni(request.getDni());
            cliente.setEmail(request.getEmail());
            cliente.setEstado(request.getEstado());

            Cliente clienteActualizado = clienteRepository.save(cliente);
            logger.info("Cliente actualizado exitosamente: {}", id);

            return mapToResponse(clienteActualizado);

        } catch (Exception e) {
            logger.error("Error al actualizar cliente ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void eliminarCliente(Long id) {
        logger.info("Intentando eliminar cliente con ID: {}", id);

        try {
            // Verificar que el cliente existe
            if (!clienteRepository.existsById(id)) {
                logger.error("Cliente no encontrado para eliminar: {}", id);
                throw new RuntimeException("Cliente no encontrado con ID: " + id);
            }

            logger.info("Eliminando cliente ID: {}", id);

            // deleteById
            clienteRepository.deleteById(id);

            // Forzar el flush de la transacción
            clienteRepository.flush();

            // Pequeña pausa para asegurar la transacción
            Thread.sleep(100);

            // Verificar que realmente se eliminó
            boolean existeDespues = clienteRepository.existsById(id);
            logger.info("Cliente aún existe después de eliminar: {}", existeDespues);

            if (existeDespues) {
                logger.error("Fallo al eliminar cliente ID: {}", id);
                throw new RuntimeException("Error: No se pudo eliminar el cliente con ID: " + id);
            }

            logger.info("Cliente eliminado exitosamente: {}", id);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupción durante eliminación: {}", e.getMessage());
            throw new RuntimeException("Error durante la eliminación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al eliminar cliente ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCliente(Long id) {
        return clienteRepository.existsById(id);
    }

    @Override
    @CircuitBreaker(name = "cuentaService", fallbackMethod = "fallbackObtenerClienteConCuentas")
    public List<CuentaResponse> obtenerClienteConCuentas(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFound("Cliente no encontrado con ID: " + idCliente);
        }
        try {
            return cuentaClient.getCuentasByClienteId(idCliente);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("No se encontraron cuentas para el cliente ID: " + idCliente);
        }
    }
    public List<CuentaResponse> fallbackObtenerClienteConCuentas(Long idCliente, Throwable t) {
        logger.error("Fallo al obtener cuentas para el cliente ID {}: {}", idCliente, t.getMessage());
        throw new RuntimeException("Servicio de cuentas no disponible. Intente más tarde.");
    }
}
