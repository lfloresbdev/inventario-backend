package pe.albrugroup.inventario_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pe.albrugroup.inventario_service.enums.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioFisicoRequestDTO {

    @NotNull(message = "La empresa es obligatoria")
    private Empresa empresa;

    @NotNull(message = "El dispositivo es obligatorio")
    private Long dispositivoId;

    @NotNull(message = "La marca es obligatoria")
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
