package pe.albrugroup.inventario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.Rol;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private String nombre;
    private String username;
    private Rol rol;
    private Empresa empresa;
    private long expiresAt;
    private String token;
}
