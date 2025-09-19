package org.nttdata.com.servicionotificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "estado_notificaciones")
@Builder
public class EstadoNotificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 50)
    private String estado; // ENVIADO, FALLIDO, PENDIENTE

    @OneToMany(mappedBy = "estadoNotificacion")
    private List<Notificacion> notificaciones;
}
