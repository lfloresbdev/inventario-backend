package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocalRepository extends JpaRepository<Local, Long> {

    List<Local> findByActivoTrue();
    List<Local> findByEmpresaAndActivoTrue(Empresa empresa);
    boolean existsByNombreAndEmpresaAndActivoTrue(String nombre, Empresa empresa);
    Optional<Local> findByNombreAndEmpresa(String nombre, Empresa empresa);
    List<Local> findByActivoFalse();
    List<Local> findByEmpresaAndActivoFalse(Empresa empresa);
}
