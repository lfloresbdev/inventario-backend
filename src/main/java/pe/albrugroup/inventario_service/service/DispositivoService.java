package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.DispositivoResponseDTO;
import pe.albrugroup.inventario_service.dto.MarcaResponseDTO;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.model.Dispositivo;
import pe.albrugroup.inventario_service.repository.DispositivoRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispositivoService {

    private final DispositivoRepository dispositivoRepository;

    public List<DispositivoResponseDTO> listar() {
        return dispositivoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public DispositivoResponseDTO crear(String nombre) {
        return crear(nombre, false);
    }

    public DispositivoResponseDTO crear(String nombre, boolean esComponenteCpu) {
        if (dispositivoRepository.existsByNombre(nombre)) {
            throw new RecursoDuplicadoException("Ya existe el dispositivo '" + nombre + "'");
        }
        Dispositivo d = new Dispositivo();
        d.setNombre(nombre);
        d.setEsComponenteCpu(esComponenteCpu);
        return toDTO(dispositivoRepository.save(d));
    }

    public DispositivoResponseDTO actualizar(Long id, String nombre) {
        Dispositivo d = obtener(id);
        return actualizar(d, nombre, d.isEsComponenteCpu());
    }

    public DispositivoResponseDTO actualizar(Long id, String nombre, boolean esComponenteCpu) {
        Dispositivo d = obtener(id);
        return actualizar(d, nombre, esComponenteCpu);
    }

    private DispositivoResponseDTO actualizar(Dispositivo d, String nombre, boolean esComponenteCpu) {
        dispositivoRepository.findByNombre(nombre).ifPresent(otro -> {
            if (!otro.getId().equals(d.getId())) {
                throw new RecursoDuplicadoException("Ya existe el dispositivo '" + nombre + "'");
            }
        });
        d.setNombre(nombre);
        d.setEsComponenteCpu(esComponenteCpu);
        return toDTO(dispositivoRepository.save(d));
    }

    public void eliminar(Long id) {
        dispositivoRepository.delete(obtener(id));
    }

    public Dispositivo obtener(Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dispositivo no encontrado: " + id));
    }

    public DispositivoResponseDTO toDTO(Dispositivo d) {
        List<MarcaResponseDTO> marcas = d.getMarcas().stream()
                .map(m -> new MarcaResponseDTO(m.getId(), m.getNombre()))
                .sorted(Comparator.comparing(MarcaResponseDTO::getNombre))
                .toList();
        return new DispositivoResponseDTO(d.getId(), d.getNombre(), d.isEsComponenteCpu(), marcas);
    }
}
