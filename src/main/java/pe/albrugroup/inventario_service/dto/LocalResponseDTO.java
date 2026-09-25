package pe.albrugroup.inventario_service.dto;

import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalResponseDTO {
    private Long id;
    private String nombre;
    private Empresa empresa;
    private boolean activo;
}
