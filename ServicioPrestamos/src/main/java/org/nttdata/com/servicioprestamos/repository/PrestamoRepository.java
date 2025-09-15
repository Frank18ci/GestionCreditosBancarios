package org.nttdata.com.servicioprestamos.repository;

import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByClienteId(Long clienteId);

    List<Prestamo> findByClienteIdAndEstadoPrestamoId(Long clienteId, Long estadoPrestamoId);
}
