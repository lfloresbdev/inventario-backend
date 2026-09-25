package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.UbicacionFisicaResponseDTO;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.model.Local;
import pe.albrugroup.inventario_service.model.UbicacionFisica;
import pe.albrugroup.inventario_service.repository.UbicacionFisicaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionFisicaService {

    private final UbicacionFisicaRepository ubicacionRepository;
    private final LocalService localService;

    public List<UbicacionFisicaResponseDTO> listar() {
        return ubicacionRepository.findByActivoTrue().stream().map(this::toDTO).toList();
    }

    public List<UbicacionFisicaResponseDTO> listarPorLocal(Long localId) {
        return ubicacionRepository.findByLocalIdAndActivoTrue(localId).stream().map(this::toDTO).toList();
    }

    public List<UbicacionFisicaResponseDTO> listarInactivos() {
        return ubicacionRepository.findByActivoFalse().stream().map(this::toDTO).toList();
    }

    public UbicacionFisicaResponseDTO darDeBaja(Long id) {
        UbicacionFisica ub = obtener(id);
        ub.setActivo(false);
        return toDTO(ubicacionRepository.save(ub));
    }

    public UbicacionFisicaResponseDTO reactivar(Long id) {
        UbicacionFisica ub = obtener(id);
        ub.setActivo(true);
        return toDTO(ubicacionRepository.save(ub));
    }

    public UbicacionFisicaResponseDTO crear(String nombre, Long localId) {
        if (ubicacionRepository.existsByNombreAndLocalId(nombre, localId)) {
            throw new RecursoDuplicadoException("Ya existe una ubicación '" + nombre + "' en ese local");
        }
        Local local = localService.obtener(localId);
        UbicacionFisica ub = new UbicacionFisica(null, nombre, local, true);
        return toDTO(ubicacionRepository.save(ub));
    }

    public UbicacionFisicaResponseDTO actualizar(Long id, String nombre, Long localId) {
        UbicacionFisica ub = obtener(id);
        Local local = localService.obtener(localId);
        ub.setNombre(nombre);
        ub.setLocal(local);
        return toDTO(ubicacionRepository.save(ub));
    }

    public UbicacionFisica obtener(Long id) {
        return ubicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación física no encontrada: " + id));
    }

    public UbicacionFisicaResponseDTO toDTO(UbicacionFisica ub) {
        return new UbicacionFisicaResponseDTO(ub.getId(), ub.getNombre(), localService.toDTO(ub.getLocal()), ub.isActivo());
    }
}
