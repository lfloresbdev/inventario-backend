package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ubicaciones_fisicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionFisica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    @Column(nullable = false)
    private boolean activo = true;
}
