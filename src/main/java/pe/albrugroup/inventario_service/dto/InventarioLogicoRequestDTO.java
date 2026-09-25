package pe.albrugroup.inventario_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.TipoAcceso;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioLogicoRequestDTO {

    @NotNull(message = "La empresa es obligatoria")
    private Empresa empresa;

    @NotNull(message = "El tipo de acceso es obligatorio")
    private TipoAcceso tipoAcceso;

    @NotBlank(message = "El identificador es obligatorio")
    private String identificador;

    private String contrasena;
    private Long estacionId;
}