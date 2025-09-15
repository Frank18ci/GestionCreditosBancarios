package org.nttdata.com.servicioprestamos.repository;

import org.nttdata.com.servicioprestamos.models.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    List<Cuota> findByPrestamoId(Long prestamoId);
}
