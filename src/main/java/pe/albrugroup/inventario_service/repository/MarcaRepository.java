package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByDispositivo_Id(Long dispositivoId);

    boolean existsByNombreAndDispositivo_Id(String nombre, Long dispositivoId);

    Optional<Marca> findByNombreAndDispositivo_Id(String nombre, Long dispositivoId);
}
