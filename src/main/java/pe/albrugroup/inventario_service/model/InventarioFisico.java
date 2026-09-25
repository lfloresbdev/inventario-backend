package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;
import pe.albrugroup.inventario_service.enums.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario_fisico")
public class InventarioFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "estacion_id", nullable = true)
    private Estacion estacion;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = true)
    private InventarioFisico parent;

    @OneToMany(mappedBy = "parent")
    private java.util.List<InventarioFisico> componentes = new java.util.ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    private String modelo;
    private String serie;

    @Enumerated(EnumType.STRING)
    @Column(name = "ram_tipo")
    private RamTipo ramTipo;

    @Column(name = "ram_espacio")
    private Integer ramEspacio;

    private String procesador;
    private Integer pulgadas;

    @Enumerated(EnumType.STRING)
    @Column(name = "disco_tipo")
    private DiscoTipo discoTipo;

    @Column(name = "disco_espacio")
    private Integer discoEspacio;

    private String hostname;
}
