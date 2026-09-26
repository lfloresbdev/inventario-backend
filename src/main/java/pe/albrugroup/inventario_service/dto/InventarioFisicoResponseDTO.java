package pe.albrugroup.inventario_service.dto;

import lombok.*;
import pe.albrugroup.inventario_service.enums.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioFisicoResponseDTO {

    private Long id;
    private Empresa empresa;
    private Long estacionId;
    private Long parentId;
    private String parentHostname;
    private DispositivoResponseDTO dispositivo;
    private MarcaResponseDTO marca;
    private String modelo;
    private String serie;
    private RamTipo ramTipo;
    private Integer ramEspacio;
    private String procesador;
    private Integer pulgadas;
    private DiscoTipo discoTipo;
    private Integer discoEspacio;
    private String hostname;
    private LocalResponseDTO local;
    private EstadoFisico estado;
}
