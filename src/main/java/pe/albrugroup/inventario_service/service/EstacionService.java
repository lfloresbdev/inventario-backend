package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albrugroup.inventario_service.dto.*;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.exception.ValidacionException;
import pe.albrugroup.inventario_service.model.Estacion;
import pe.albrugroup.inventario_service.model.InventarioFisico;
import pe.albrugroup.inventario_service.model.Local;
import pe.albrugroup.inventario_service.model.UbicacionFisica;
import pe.albrugroup.inventario_service.repository.EstacionRepository;
import pe.albrugroup.inventario_service.repository.InventarioFisicoRepository;
import pe.albrugroup.inventario_service.repository.InventarioLogicoRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstacionService {

    private final EstacionRepository estacionRepository;
    private final InventarioFisicoRepository fisicoRepository;
    private final InventarioLogicoRepository logicoRepository;
    private final LocalService localService;
    private final UbicacionFisicaService ubicacionFisicaService;

    public List<EstacionResponseDTO> listar() {
        return estacionRepository.findByActivoTrue().stream().map(this::toResponseDTO).toList();
    }

    public EstacionResponseDTO buscarPorId(Long id) {
        return toResponseDTO(obtenerEntidad(id));
    }

    public List<EstacionResponseDTO> listarPorLocal(Long localId) {
        return estacionRepository.findByLocalIdAndActivoTrue(localId).stream().map(this::toResponseDTO).toList();
    }

    public List<EstacionResponseDTO> listarPorEmpresa(Empresa empresa) {
        return estacionRepository.findByEmpresaAndActivoTrue(empresa).stream().map(this::toResponseDTO).toList();
    }

    public List<EstacionResponseDTO> listarInactivos() {
        return estacionRepository.findByActivoFalse().stream().map(this::toResponseDTO).toList();
    }

    @Transactional
    public EstacionResponseDTO darDeBaja(Long id) {
        Estacion estacion = obtenerEntidad(id);
        estacion.setActivo(false);
        desasignarInventario(id);
        return toResponseDTO(estacionRepository.save(estacion));
    }

    public EstacionResponseDTO reactivar(Long id) {
        Estacion estacion = obtenerEntidad(id);
        estacion.setActivo(true);
        return toResponseDTO(estacionRepository.save(estacion));
    }

    @Transactional
    public EstacionResponseDTO crear(EstacionCreateDTO dto) {
        Local local = localService.obtener(dto.getLocalId());
        UbicacionFisica ubicacion = ubicacionFisicaService.obtener(dto.getUbicacionFisicaId());

        List<InventarioFisico> items = resolverItems(dto.getItemIds());
        validarEmpresaItems(dto.getEmpresa(), items);
        validarConfiguracion(items);

        int numero = estacionRepository.findMaxNumero(dto.getLocalId(), dto.getUbicacionFisicaId()) + 1;

        Estacion estacion = new Estacion();
        estacion.setEmpresa(dto.getEmpresa());
        estacion.setLocal(local);
        estacion.setUbicacionFisica(ubicacion);
        estacion.setNumero(numero);
        estacion.setNombre(generarNombre(local, ubicacion, numero));
        Estacion guardada = estacionRepository.save(estacion);

        for (InventarioFisico item : items) {
            item.setEstacion(guardada);
            fisicoRepository.save(item);
        }

        return toResponseDTO(guardada);
    }

    public EstacionResponseDTO actualizar(Long id, EstacionUpdateDTO dto) {
        Estacion estacion = obtenerEntidad(id);

        Local local = localService.obtener(dto.getLocalId());
        UbicacionFisica ubicacion = ubicacionFisicaService.obtener(dto.getUbicacionFisicaId());

        Optional<Estacion> duplicada = estacionRepository
                .findByLocalIdAndUbicacionFisicaIdAndNumero(dto.getLocalId(), dto.getUbicacionFisicaId(), estacion.getNumero());
        if (duplicada.isPresent() && !duplicada.get().getId().equals(id)) {
            throw new RecursoDuplicadoException("Ya existe otra estación con ese local, ubicación y número");
        }

        estacion.setEmpresa(dto.getEmpresa());
        estacion.setLocal(local);
        estacion.setUbicacionFisica(ubicacion);
        estacion.setNombre(generarNombre(local, ubicacion, estacion.getNumero()));
        return toResponseDTO(estacionRepository.save(estacion));
    }

    @Transactional
    public void eliminar(Long id) {
        Estacion estacion = obtenerEntidad(id);
        desasignarInventario(id);
        estacionRepository.delete(estacion);
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

    private List<InventarioFisico> resolverItems(List<Long> itemIds) {
        return itemIds.stream().map(itemId ->
                fisicoRepository.findById(itemId)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Item físico no encontrado: " + itemId))
        ).collect(Collectors.toList());
    }

    private void validarEmpresaItems(Empresa empresa, List<InventarioFisico> items) {
        for (InventarioFisico item : items) {
            if (item.getEmpresa() != empresa) {
                throw new ValidacionException(
                        "El item #" + item.getId() + " pertenece a " + item.getEmpresa() + ", no a " + empresa);
            }
            if (item.getEstacion() != null) {
                throw new ValidacionException(
                        "El item #" + item.getId() + " ya está asignado a la estación " + item.getEstacion().getNombre());
            }
        }
    }

    private void validarConfiguracion(List<InventarioFisico> items) {
        Map<String, Long> conteo = items.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getDispositivo().getNombre().toUpperCase(),
                        Collectors.counting()
                ));

        boolean esLaptop = conteo.getOrDefault("LAPTOP", 0L) == 1
                && conteo.getOrDefault("CARGADOR", 0L) == 1
                && conteo.size() == 2;

        boolean tieneVgaOHdmi = conteo.getOrDefault("CABLE VGA", 0L) == 1
                || conteo.getOrDefault("CABLE HDMI", 0L) == 1;
        long totalCableVideo = conteo.getOrDefault("CABLE VGA", 0L) + conteo.getOrDefault("CABLE HDMI", 0L);

        boolean esEscritorio = conteo.getOrDefault("CPU", 0L) == 1
                && conteo.getOrDefault("MONITOR", 0L) == 1
                && conteo.getOrDefault("TECLADO", 0L) == 1
                && conteo.getOrDefault("MOUSE", 0L) == 1
                && conteo.getOrDefault("AUDÍFONOS", 0L) == 1
                && conteo.getOrDefault("CABLE PODER", 0L) == 2
                && tieneVgaOHdmi && totalCableVideo == 1
                && conteo.size() == 7;

        if (!esLaptop && !esEscritorio) {
            throw new ValidacionException(
                    "Configuración inválida. Se requiere: " +
                    "Escritorio (1 CPU + 1 MONITOR + 1 TECLADO + 1 MOUSE + 1 AUDÍFONOS + 2 CABLE PODER + 1 CABLE VGA o HDMI) " +
                    "o Laptop (1 LAPTOP + 1 CARGADOR).");
        }
    }

    private Estacion obtenerEntidad(Long id) {
        return estacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estación no encontrada: " + id));
    }

    private String generarNombre(Local local, UbicacionFisica ubicacion, Integer numero) {
        return local.getNombre() + "-" + ubicacion.getNombre() + "-" + numero;
    }

    private EstacionResponseDTO toResponseDTO(Estacion estacion) {
        LocalResponseDTO localDTO = localService.toDTO(estacion.getLocal());
        UbicacionFisicaResponseDTO ubicacionDTO = ubicacionFisicaService.toDTO(estacion.getUbicacionFisica());
        return new EstacionResponseDTO(
                estacion.getId(),
                estacion.getEmpresa(),
                estacion.getNombre(),
                localDTO,
                ubicacionDTO,
                estacion.getNumero(),
                estacion.getCreadoEn(),
                estacion.isActivo());
    }
}
