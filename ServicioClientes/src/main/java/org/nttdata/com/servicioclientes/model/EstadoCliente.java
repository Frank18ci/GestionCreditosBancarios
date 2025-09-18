package org.nttdata.com.servicioclientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "estado_clientes")
public class EstadoCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 50)
    private String estado; // ACTIVO, INACTIVO, SUSPENDIDO

    @OneToMany(mappedBy = "estadoCliente")
    private List<Cliente> clientes;
}
