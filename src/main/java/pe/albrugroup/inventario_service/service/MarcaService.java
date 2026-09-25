package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.MarcaResponseDTO;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.exception.ValidacionException;
import pe.albrugroup.inventario_service.model.Dispositivo;
import pe.albrugroup.inventario_service.model.Marca;
import pe.albrugroup.inventario_service.repository.DispositivoRepository;
import pe.albrugroup.inventario_service.repository.MarcaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final DispositivoRepository dispositivoRepository;

    public List<MarcaResponseDTO> listarPorDispositivo(Long dispositivoId) {
        validarDispositivo(dispositivoId);
        return marcaRepository.findByDispositivo_Id(dispositivoId).stream().map(this::toDTO).toList();
    }

    public MarcaResponseDTO crear(Long dispositivoId, String nombre) {
        Dispositivo dispositivo = validarDispositivo(dispositivoId);
        if (marcaRepository.existsByNombreAndDispositivo_Id(nombre, dispositivoId)) {
            throw new RecursoDuplicadoException(
                    "Ya existe la marca '" + nombre + "' para el dispositivo " + dispositivo.getNombre());
        }
        Marca marca = new Marca();
        marca.setNombre(nombre);
        marca.setDispositivo(dispositivo);
        return toDTO(marcaRepository.save(marca));
    }

    public MarcaResponseDTO actualizar(Long dispositivoId, Long marcaId, String nombre) {
        Marca marca = obtenerDeDispositivo(dispositivoId, marcaId);
        marcaRepository.findByNombreAndDispositivo_Id(nombre, dispositivoId).ifPresent(otra -> {
            if (!otra.getId().equals(marcaId)) {
                throw new RecursoDuplicadoException(
                        "Ya existe la marca '" + nombre + "' para ese dispositivo");
            }
        });
        marca.setNombre(nombre);
        return toDTO(marcaRepository.save(marca));
    }

    public void eliminar(Long dispositivoId, Long marcaId) {
        marcaRepository.delete(obtenerDeDispositivo(dispositivoId, marcaId));
    }

    public Marca obtener(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Marca no encontrada: " + id));
    }

    private Dispositivo validarDispositivo(Long dispositivoId) {
        return dispositivoRepository.findById(dispositivoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dispositivo no encontrado: " + dispositivoId));
    }

    private Marca obtenerDeDispositivo(Long dispositivoId, Long marcaId) {
        Marca marca = obtener(marcaId);
        if (marca.getDispositivo() == null || !marca.getDispositivo().getId().equals(dispositivoId)) {
            throw new ValidacionException("La marca no pertenece al dispositivo indicado");
        }
        return marca;
    }

    public MarcaResponseDTO toDTO(Marca marca) {
        return new MarcaResponseDTO(marca.getId(), marca.getNombre());
    }
}
