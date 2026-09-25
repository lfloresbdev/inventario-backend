package pe.albrugroup.inventario_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstacionCreateDTO {

    @NotNull(message = "La empresa es obligatoria")
    private Empresa empresa;

    @NotNull(message = "El local es obligatorio")
    private Long localId;

    @NotNull(message = "La ubicación física es obligatoria")
    private Long ubicacionFisicaId;

    @NotEmpty(message = "Debe seleccionar al menos un item del inventario físico")
    private List<Long> itemIds;
}
