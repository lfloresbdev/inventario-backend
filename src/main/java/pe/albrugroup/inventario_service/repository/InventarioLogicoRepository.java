package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.TipoAcceso;
import pe.albrugroup.inventario_service.model.InventarioLogico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioLogicoRepository extends JpaRepository<InventarioLogico, Long> {

    boolean existsByEstacion_IdAndTipoAcceso(Long estacionId, TipoAcceso tipoAcceso);

    List<InventarioLogico> findByEmpresa(Empresa empresa);
    List<InventarioLogico> findByEstacionId(Long estacionId);
    List<InventarioLogico> findByTipoAcceso(TipoAcceso tipoAcceso);
    List<InventarioLogico> findByEstacionIdAndTipoAcceso(Long estacionId, TipoAcceso tipoAcceso);
    List<InventarioLogico> findByEstacion_UbicacionFisica_Id(Long ubicacionId);
    List<InventarioLogico> findByEstacion_Local_Id(Long localId);

}