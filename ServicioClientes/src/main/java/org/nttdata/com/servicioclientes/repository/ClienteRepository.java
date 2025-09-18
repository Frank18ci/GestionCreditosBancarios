package org.nttdata.com.servicioclientes.repository;

import jakarta.transaction.Transactional;
import org.nttdata.com.servicioclientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE c.estadoCliente.id = :estado")
    List<Cliente> findByEstado(@Param("estadoId") Long estadoId);

    List<Cliente> findByNombreContaining(String nombre);


    @Transactional
    @Modifying
    @Query("DELETE FROM Cliente c WHERE c.id = :id")
    void eliminarPorId(@Param("id") Long id);


    boolean existsById(Long id);

    Optional<Cliente> findByKeycloakId(String keycloakId);
}
