package pe.albrugroup.inventario_service.dto;

import lombok.*;
import pe.albrugroup.inventario_service.enums.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioFisicoUpdateDTO {
    private Empresa empresa;
    private Long dispositivoId;
    private Long marcaId;
    private String modelo;
    private String serie;
    private RamTipo ramTipo;
    private Integer ramEspacio;
    private String procesador;
    private Integer pulgadas;
    private DiscoTipo discoTipo;
    private Integer discoEspacio;
    private Long estacionId;
    private String hostname;
}
