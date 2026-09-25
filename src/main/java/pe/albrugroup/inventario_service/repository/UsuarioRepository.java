package pe.albrugroup.inventario_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByEmpresa(Empresa empresa);
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);

}
