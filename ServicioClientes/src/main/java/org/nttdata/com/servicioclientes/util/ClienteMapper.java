package org.nttdata.com.servicioclientes.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nttdata.com.servicioclientes.dto.ClienteRequest;
import org.nttdata.com.servicioclientes.dto.ClienteResponse;
import org.nttdata.com.servicioclientes.model.Cliente;

import java.util.List;

@Mapper(componentModel = "spring", uses = EstadoClienteMapper.class)
public interface ClienteMapper {
    @Mapping(source = "estadoClienteId", target = "estadoCliente.id")
    Cliente toEntity(ClienteRequest clienteRequest);
    ClienteResponse toDto(Cliente cliente);
    List<ClienteResponse> toDtoList(List<Cliente> clientes);
}
