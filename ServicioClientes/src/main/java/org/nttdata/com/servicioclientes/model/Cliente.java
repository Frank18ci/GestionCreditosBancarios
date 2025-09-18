package org.nttdata.com.servicioclientes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @ManyToOne
    private EstadoCliente estadoCliente;

    private String keycloakId;
}
