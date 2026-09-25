package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.model.UbicacionFisica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UbicacionFisicaRepository extends JpaRepository<UbicacionFisica, Long> {

    List<UbicacionFisica> findByLocalId(Long localId);
    boolean existsByNombreAndLocalId(String nombre, Long localId);
    List<UbicacionFisica> findByActivoTrue();
    List<UbicacionFisica> findByLocalIdAndActivoTrue(Long localId);
    List<UbicacionFisica> findByActivoFalse();
    List<UbicacionFisica> findByLocalIdAndActivoFalse(Long localId);
}
