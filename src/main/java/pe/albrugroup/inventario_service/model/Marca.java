package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marcas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "dispositivo_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;
}
