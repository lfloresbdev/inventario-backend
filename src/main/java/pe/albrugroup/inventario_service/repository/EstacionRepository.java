package pe.albrugroup.inventario_service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstacionRepository extends JpaRepository<Estacion, Long> {

    List<Estacion> findByEmpresa(Empresa empresa);
    List<Estacion> findByLocalId(Long localId);
    List<Estacion> findByActivoTrue();
    List<Estacion> findByLocalIdAndActivoTrue(Long localId);
    List<Estacion> findByEmpresaAndActivoTrue(Empresa empresa);
    List<Estacion> findByActivoFalse();
    Optional<Estacion> findByLocalIdAndUbicacionFisicaIdAndNumero(Long localId, Long ubicacionFisicaId, Integer numero);
    boolean existsByLocalIdAndUbicacionFisicaIdAndNumero(Long localId, Long ubicacionFisicaId, Integer numero);

    @Query("SELECT COALESCE(MAX(e.numero), 0) FROM Estacion e WHERE e.local.id = :localId AND e.ubicacionFisica.id = :ubicacionFisicaId")
    int findMaxNumero(@Param("localId") Long localId, @Param("ubicacionFisicaId") Long ubicacionFisicaId);
}
