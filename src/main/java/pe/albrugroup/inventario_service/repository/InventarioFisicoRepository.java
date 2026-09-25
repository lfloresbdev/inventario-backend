package pe.albrugroup.inventario_service.repository;

import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.model.InventarioFisico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioFisicoRepository extends JpaRepository<InventarioFisico, Long> {

    boolean existsByEstacion_IdAndDispositivo_Id(Long estacionId, Long dispositivoId);
    long countByEstacion_IdAndDispositivo_Id(Long estacionId, Long dispositivoId);

    // listas principales
    List<InventarioFisico> findByEmpresa(Empresa empresa);
    List<InventarioFisico> findByEmpresaAndEstacionIsNullAndParentIsNull(Empresa empresa);
    List<InventarioFisico> findByEstacionId(Long estacionId);
    List<InventarioFisico> findByDispositivo_Id(Long dispositivoId);
    List<InventarioFisico> findByEstacion_Local_IdAndParentIsNull(Long localId);
    List<InventarioFisico> findByEstacion_UbicacionFisica_IdAndParentIsNull(Long ubicacionId);

    // componentes de un CPU
    List<InventarioFisico> findByParent_Id(Long parentId);

    // componentes libres (sin CPU ni estación) para asignar
    List<InventarioFisico> findByEmpresaAndParentIsNullAndEstacionIsNull(Empresa empresa);
}
