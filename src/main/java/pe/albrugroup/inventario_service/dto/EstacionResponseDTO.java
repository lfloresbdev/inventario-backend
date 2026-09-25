package pe.albrugroup.inventario_service.dto;

import lombok.*;
import pe.albrugroup.inventario_service.enums.Empresa;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstacionResponseDTO {

    private Long id;
    private Empresa empresa;
    private String nombre;
    private LocalResponseDTO local;
    private UbicacionFisicaResponseDTO ubicacionFisica;
    private Integer numero;
    private LocalDateTime creadoEn;
    private boolean activo;
}
