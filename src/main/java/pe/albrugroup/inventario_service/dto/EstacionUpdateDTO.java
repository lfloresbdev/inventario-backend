package pe.albrugroup.inventario_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstacionUpdateDTO {

    @NotNull(message = "La empresa es obligatoria")
    private Empresa empresa;

    @NotNull(message = "El local es obligatorio")
    private Long localId;

    @NotNull(message = "La ubicación física es obligatoria")
    private Long ubicacionFisicaId;
}
