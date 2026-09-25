package pe.albrugroup.inventario_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoResponseDTO {
    private Long id;
    private String nombre;
    private boolean esComponenteCpu;
    private List<MarcaResponseDTO> marcas;
}
