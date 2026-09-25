package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.albrugroup.inventario_service.converter.SecretEncryptionConverter;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.TipoAcceso;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario_logico")
public class InventarioLogico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "estacion_id", nullable = true)
    private Estacion estacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acceso", nullable = false)
    private TipoAcceso tipoAcceso;

    @Column(nullable = false)
    private String identificador;

    @Convert(converter = SecretEncryptionConverter.class)
    private String contrasena;
}