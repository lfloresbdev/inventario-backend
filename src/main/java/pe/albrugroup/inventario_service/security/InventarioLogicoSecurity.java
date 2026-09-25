package pe.albrugroup.inventario_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.model.InventarioLogico;
import pe.albrugroup.inventario_service.model.Usuario;
import pe.albrugroup.inventario_service.repository.InventarioLogicoRepository;
import pe.albrugroup.inventario_service.repository.UsuarioRepository;

@Component("ilSec")
@RequiredArgsConstructor
public class InventarioLogicoSecurity {

    private final InventarioLogicoRepository logicoRepository;
    private final UsuarioRepository usuarioRepository;

    public boolean puedeVerItem(Long id, Authentication auth) {
        if (isAdmin(auth)) return true;
        Usuario usuario = getUsuario(auth);
        if (usuario == null) return false;
        InventarioLogico item = logicoRepository.findById(id).orElse(null);
        return item != null && item.getEmpresa() == usuario.getEmpresa();
    }

    public boolean puedeVerEmpresa(Empresa empresa, Authentication auth) {
        if (isAdmin(auth)) return true;
        Usuario usuario = getUsuario(auth);
        if (usuario == null) return false;
        return empresa == usuario.getEmpresa();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByUsername(auth.getName()).orElse(null);
    }
}
