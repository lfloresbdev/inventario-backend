package pe.albrugroup.inventario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.EstadoLogico;
import pe.albrugroup.inventario_service.enums.TipoAcceso;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class InventarioLogicoUpdateDTO {
    private Empresa empresa;            // opcional
    private TipoAcceso tipoAcceso;      // opcional
    private String identificador;       // opcional
    private String contrasena;          // opcional — permite cambiar contraseña vía PATCH
    private Long estacionId;
    private EstadoLogico estado;        // opcional
}