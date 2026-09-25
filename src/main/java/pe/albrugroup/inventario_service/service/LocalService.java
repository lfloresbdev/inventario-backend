package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albrugroup.inventario_service.dto.LocalResponseDTO;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.model.Estacion;
import pe.albrugroup.inventario_service.model.Local;
import pe.albrugroup.inventario_service.model.UbicacionFisica;
import pe.albrugroup.inventario_service.repository.EstacionRepository;
import pe.albrugroup.inventario_service.repository.InventarioFisicoRepository;
import pe.albrugroup.inventario_service.repository.InventarioLogicoRepository;
import pe.albrugroup.inventario_service.repository.LocalRepository;
import pe.albrugroup.inventario_service.repository.UbicacionFisicaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalService {

    private final LocalRepository localRepository;
    private final UbicacionFisicaRepository ubicacionFisicaRepository;
    private final EstacionRepository estacionRepository;
    private final InventarioFisicoRepository fisicoRepository;
    private final InventarioLogicoRepository logicoRepository;

    public List<LocalResponseDTO> listar() {
        return localRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    public List<LocalResponseDTO> listarPorEmpresa(Empresa empresa) {
        return localRepository.findByEmpresaAndActivoTrue(empresa).stream().map(this::toDTO).toList();
    }

    public List<LocalResponseDTO> listarInactivos() {
        return localRepository.findByActivoFalse().stream().map(this::toDTO).toList();
    }

    public List<LocalResponseDTO> listarInactivosPorEmpresa(Empresa empresa) {
        return localRepository.findByEmpresaAndActivoFalse(empresa).stream().map(this::toDTO).toList();
    }

    @Transactional
    public LocalResponseDTO crear(String nombre, Empresa empresa) {
        Optional<Local> existente = localRepository.findByNombreAndEmpresa(nombre, empresa);
        if (existente.isPresent()) {
            Local local = existente.get();
            if (local.isActivo()) {
                throw new RecursoDuplicadoException("Ya existe un local '" + nombre + "' para " + empresa);
            }
            local.setActivo(true);
            return toDTO(localRepository.save(local));
        }
        Local local = new Local(null, nombre, empresa, true);
        return toDTO(localRepository.save(local));
    }

    public LocalResponseDTO actualizar(Long id, String nombre, Empresa empresa) {
        Local local = obtener(id);
        localRepository.findByNombreAndEmpresa(nombre, empresa).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                String sufijo = otro.isActivo() ? "" : " (dado de baja)";
                throw new RecursoDuplicadoException("Ya existe otro local '" + nombre + "' para " + empresa + sufijo);
            }
        });
        local.setNombre(nombre);
        local.setEmpresa(empresa);
        return toDTO(localRepository.save(local));
    }

    @Transactional
    public LocalResponseDTO darDeBaja(Long id) {
        Local local = obtener(id);
        local.setActivo(false);
        for (UbicacionFisica ubicacion : ubicacionFisicaRepository.findByLocalId(id)) {
            ubicacion.setActivo(false);
            ubicacionFisicaRepository.save(ubicacion);
        }
        for (Estacion estacion : estacionRepository.findByLocalId(id)) {
            estacion.setActivo(false);
            estacionRepository.save(estacion);
            desasignarInventario(estacion.getId());
        }
        return toDTO(localRepository.save(local));
    }

    public LocalResponseDTO reactivar(Long id) {
        Local local = obtener(id);
        local.setActivo(true);
        return toDTO(localRepository.save(local));
    }

    private void desasignarInventario(Long estacionId) {
        fisicoRepository.findByEstacionId(estacionId).forEach(item -> {
            item.setEstacion(null);
            fisicoRepository.save(item);
        });
        logicoRepository.findByEstacionId(estacionId).forEach(acceso -> {
            acceso.setEstacion(null);
            logicoRepository.save(acceso);
        });
    }

    public Local obtener(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Local no encontrado: " + id));
    }

    public LocalResponseDTO toDTO(Local local) {
        return new LocalResponseDTO(local.getId(), local.getNombre(), local.getEmpresa(), local.isActivo());
    }
}
