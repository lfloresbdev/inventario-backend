package pe.albrugroup.inventario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.EstadoLogico;
import pe.albrugroup.inventario_service.enums.TipoAcceso;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioLogicoResponseDTO {

    private Long id;
    private Empresa empresa;
    private Long estacionId;
    private TipoAcceso tipoAcceso;
    private String identificador;
    private EstadoLogico estado;
    // sin "contrasena" a propósito — se consulta aparte vía ContrasenaResponseDTO
}