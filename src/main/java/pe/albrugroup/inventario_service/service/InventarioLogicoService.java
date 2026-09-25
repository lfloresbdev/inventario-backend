package pe.albrugroup.inventario_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.*;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.TipoAcceso;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.model.Estacion;
import pe.albrugroup.inventario_service.model.InventarioLogico;
import pe.albrugroup.inventario_service.repository.EstacionRepository;
import pe.albrugroup.inventario_service.repository.InventarioLogicoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioLogicoService {

    private final InventarioLogicoRepository logicoRepository;
    private final EstacionRepository estacionRepository;

    public List<InventarioLogicoResponseDTO> listarPorEstacion(Long estacionId) {
        validarEstacion(estacionId);
        return logicoRepository.findByEstacionId(estacionId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<InventarioLogicoResponseDTO> listarPorTipoAcceso(TipoAcceso tipoAcceso) {
        return logicoRepository.findByTipoAcceso(tipoAcceso).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<InventarioLogicoResponseDTO> listarPorEmpresa(Empresa empresa) {
        return logicoRepository.findByEmpresa(empresa).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<InventarioLogicoResponseDTO> listarPorUbicacion(Long ubicacionId) {
        return logicoRepository.findByEstacion_UbicacionFisica_Id(ubicacionId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public InventarioLogicoResponseDTO crear(InventarioLogicoRequestDTO dto) {
        Estacion estacion = dto.getEstacionId() != null ? validarEstacion(dto.getEstacionId()) : null;
        validarEmpresaCoincide(dto.getEmpresa(), estacion);
        InventarioLogico logico = new InventarioLogico();
        logico.setEmpresa(dto.getEmpresa());
        logico.setEstacion(estacion);
        logico.setTipoAcceso(dto.getTipoAcceso());
        logico.setIdentificador(dto.getIdentificador());
        logico.setContrasena(dto.getContrasena());
        return toResponseDTO(logicoRepository.save(logico));
    }

    public ContrasenaResponseDTO obtenerContrasena(Long id) {
        InventarioLogico logico = obtenerEntidad(id);
        return new ContrasenaResponseDTO(logico.getContrasena());
    }

    public InventarioLogicoResponseDTO actualizarParcialmente(Long id, InventarioLogicoUpdateDTO dto) {
        InventarioLogico logico = obtenerEntidad(id);

        if (dto.getTipoAcceso() != null) logico.setTipoAcceso(dto.getTipoAcceso());
        if (dto.getIdentificador() != null) logico.setIdentificador(dto.getIdentificador());
        if (dto.getContrasena() != null) logico.setContrasena(dto.getContrasena());
        if (dto.getEstacionId() != null) {
            Estacion estacion = validarEstacion(dto.getEstacionId());
            validarEmpresaCoincide(logico.getEmpresa(), estacion);
            logico.setEstacion(estacion);
        }
        if (dto.getEmpresa() != null) {
            validarEmpresaCoincide(dto.getEmpresa(), logico.getEstacion());
            logico.setEmpresa(dto.getEmpresa());
        }

        return toResponseDTO(logicoRepository.save(logico));
    }

    public InventarioLogicoResponseDTO asignarAEstacion(Long itemId, Long nuevaEstacionId) {
        InventarioLogico item = logicoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item lógico no encontrado"));

        Estacion nuevaEstacion = estacionRepository.findById(nuevaEstacionId)
                .orElseThrow(() -> new EntityNotFoundException("Estación no encontrada"));

        validarEmpresaCoincide(item.getEmpresa(), nuevaEstacion);

        boolean yaExisteMismoTipoAcceso = logicoRepository.existsByEstacion_IdAndTipoAcceso(
                nuevaEstacionId, item.getTipoAcceso()
        );

        if (yaExisteMismoTipoAcceso) {
            throw new IllegalStateException(
                    "La estación ya tiene un item con el tipo de acceso " + item.getTipoAcceso()
            );
        }

        item.setEstacion(nuevaEstacion);
        return toResponseDTO(logicoRepository.save(item));
    }

    public InventarioLogicoResponseDTO quitarDeEstacion(Long itemId) {
        InventarioLogico item = logicoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item lógico no encontrado"));

        item.setEstacion(null);
        return toResponseDTO(logicoRepository.save(item));
    }

    public void eliminar(Long id) {
        logicoRepository.delete(obtenerEntidad(id));
    }

    private Estacion validarEstacion(Long estacionId) {
        return estacionRepository.findById(estacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estación no encontrada: " + estacionId));
    }

    private void validarEmpresaCoincide(Empresa empresa, Estacion estacion) {
        if (empresa != null && estacion != null && estacion.getEmpresa() != empresa) {
            throw new IllegalStateException(
                    "La empresa del item (" + empresa + ") no coincide con la empresa de la estación ("
                            + estacion.getEmpresa() + ")");
        }
    }

    private InventarioLogico obtenerEntidad(Long id) {
        return logicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acceso lógico no encontrado: " + id));
    }

    private InventarioLogicoResponseDTO toResponseDTO(InventarioLogico l) {
        Estacion estacion = l.getEstacion();
        Long estacionId = estacion != null ? estacion.getId() : null;
        return new InventarioLogicoResponseDTO(
                l.getId(),
                l.getEmpresa(),
                estacionId,
                l.getTipoAcceso(),
                l.getIdentificador());
        // sin "contrasena" a propósito

    }
}