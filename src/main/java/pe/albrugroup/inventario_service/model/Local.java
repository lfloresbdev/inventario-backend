package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

@Entity
@Table(name = "locales", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "empresa"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private boolean activo = true;
}
