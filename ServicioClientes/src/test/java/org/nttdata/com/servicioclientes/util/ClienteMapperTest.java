package org.nttdata.com.servicioclientes.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.model.Cliente;
import org.nttdata.com.servicioclientes.model.EstadoCliente;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteMapperTest {
    private final ClienteMapper mapper = Mappers.getMapper(ClienteMapper.class);

    private final EstadoClienteMapper estadoClienteMapper = Mappers.getMapper(EstadoClienteMapper.class);
    @Test
    @DisplayName("Debe mapear ClienteRequest a Cliente correctamente")
    void testToEntity() {

        ClienteRequest request = ClienteRequest.builder()
                .dni("12345678")
                .nombre("Juan Perez")
                .email("juanperez@gmail.com")
                .estadoClienteId(1L)
                .build();

        Cliente entity = mapper.toEntity(request);

        assertAll(
                () -> assertEquals(Cliente.class, entity.getClass()),
                () -> assertEquals("12345678", entity.getDni()),
                () -> assertEquals("Juan Perez", entity.getNombre()),
                () -> assertEquals("juanperez@gmail.com", entity.getEmail()),
                () -> assertEquals(1L, entity.getEstadoCliente().getId())
        );
    }
    @Test
    @DisplayName("Debe fallar al mapear ClienteRequest a Cliente incorrectamente")
    void testFaildToEntity() {

        ClienteRequest request = ClienteRequest.builder()
                .dni("87654321")
                .nombre("Juan")
                .email("juanperez@gmail.com")
                .estadoClienteId(1L)
                .build();

        Cliente entity = mapper.toEntity(request);

        assertAll(
                () -> assertNotEquals("12345678", entity.getDni()),
                () -> assertNotEquals("Juan Perez", entity.getNombre())
        );
    }



}
