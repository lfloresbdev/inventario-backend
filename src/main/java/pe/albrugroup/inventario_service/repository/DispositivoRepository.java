package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    boolean existsByNombre(String nombre);

    Optional<Dispositivo> findByNombre(String nombre);
}
