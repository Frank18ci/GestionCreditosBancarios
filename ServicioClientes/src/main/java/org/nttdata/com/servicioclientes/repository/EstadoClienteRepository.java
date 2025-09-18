package org.nttdata.com.servicioclientes.repository;

import org.nttdata.com.servicioclientes.model.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoClienteRepository extends JpaRepository<EstadoCliente, Long> {
}
