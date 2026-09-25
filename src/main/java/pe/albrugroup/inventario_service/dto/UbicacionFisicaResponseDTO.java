package pe.albrugroup.inventario_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionFisicaResponseDTO {
    private Long id;
    private String nombre;
    private LocalResponseDTO local;
    private boolean activo;
}
