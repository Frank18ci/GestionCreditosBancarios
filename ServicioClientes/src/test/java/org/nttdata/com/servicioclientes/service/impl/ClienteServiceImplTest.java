package org.nttdata.com.servicioclientes.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioclientes.client.CuentaClient;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.exception.ResourceNotFound;
import org.nttdata.com.servicioclientes.model.Cliente;
import org.nttdata.com.servicioclientes.repository.ClienteRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CuentaClient cuentaClient;

    @InjectMocks
    private ClienteServiceImpl clienteService;



    @Test
    void obtenerClientePorId_throwsExceptionWhenClienteNotFound() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> clienteService.obtenerClientePorId(1L));
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
    }






    @Test
    void obtenerClienteConCuentas_throwsExceptionWhenClienteNotFound() {
        when(clienteRepository.existsById(1L)).thenReturn(false);

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> clienteService.obtenerClienteConCuentas(1L));
        assertEquals("Cliente no encontrado con ID: 1", exception.getMessage());
    }
}
