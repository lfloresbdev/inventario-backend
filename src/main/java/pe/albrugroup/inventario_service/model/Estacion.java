package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "estaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"local_id", "ubicacion_fisica_id", "numero"})
})
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "local_id", nullable = false)
    private Local local;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ubicacion_fisica_id", nullable = false)
    private UbicacionFisica ubicacionFisica;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "estacion")
    private List<InventarioFisico> dispositivos = new ArrayList<>();

    @OneToMany(mappedBy = "estacion")
    private List<InventarioLogico> accesos = new ArrayList<>();

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
    }
}
