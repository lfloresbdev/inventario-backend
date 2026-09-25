package pe.albrugroup.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispositivos")
@Getter
@Setter
@NoArgsConstructor
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private boolean esComponenteCpu = false;

    @OneToMany(mappedBy = "dispositivo", fetch = FetchType.EAGER)
    private List<Marca> marcas = new ArrayList<>();
}
