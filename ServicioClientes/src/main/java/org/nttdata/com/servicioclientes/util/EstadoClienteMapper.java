package org.nttdata.com.servicioclientes.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.nttdata.com.servicioclientes.dto.EstadoClienteRequest;
import org.nttdata.com.servicioclientes.dto.EstadoClienteResponse;
import org.nttdata.com.servicioclientes.model.EstadoCliente;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstadoClienteMapper {
    EstadoCliente toEntity(EstadoClienteRequest estadoClienteRequest);
    EstadoClienteResponse toDto(EstadoCliente estadoCliente);
    List<EstadoClienteResponse> toDtoList(List<EstadoCliente> estadoClientes);
}
