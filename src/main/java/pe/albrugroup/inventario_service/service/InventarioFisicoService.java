package pe.albrugroup.inventario_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.*;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.EstadoFisico;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.exception.ValidacionException;
import pe.albrugroup.inventario_service.model.*;
import pe.albrugroup.inventario_service.repository.EstacionRepository;
import pe.albrugroup.inventario_service.repository.InventarioFisicoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioFisicoService {

    private static final java.util.Map<String, Integer> MAX_POR_ESTACION = java.util.Map.of("CABLE PODER", 2);

    private final InventarioFisicoRepository fisicoRepository;
    private final EstacionRepository estacionRepository;
    private final DispositivoService dispositivoService;
    private final MarcaService marcaService;
    private final LocalService localService;

    public List<InventarioFisicoResponseDTO> listarPorEstacion(Long estacionId) {
        validarEstacion(estacionId);
        return fisicoRepository.findByEstacionId(estacionId).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarPorDispositivo(Long dispositivoId) {
        return fisicoRepository.findByDispositivo_Id(dispositivoId).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarPorLocal(Long localId) {
        return fisicoRepository.findByEstacion_Local_IdAndParentIsNull(localId).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarPorUbicacion(Long ubicacionId) {
        return fisicoRepository.findByEstacion_UbicacionFisica_IdAndParentIsNull(ubicacionId).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarPorEmpresa(Empresa empresa) {
        return fisicoRepository.findByEmpresa(empresa).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarSinAsignarPorEmpresa(Empresa empresa) {
        return fisicoRepository.findByEmpresaAndEstacionIsNullAndParentIsNull(empresa).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarComponentes(Long cpuId) {
        return fisicoRepository.findByParent_Id(cpuId).stream().map(this::toResponseDTO).toList();
    }

    public List<InventarioFisicoResponseDTO> listarLibresPorEmpresa(Empresa empresa) {
        return fisicoRepository.findByEmpresaAndParentIsNullAndEstacionIsNull(empresa).stream().map(this::toResponseDTO).toList();
    }

    public InventarioFisicoResponseDTO asignarACpu(Long componenteId, Long cpuId) {
        InventarioFisico componente = obtenerEntidad(componenteId);
        InventarioFisico cpu = fisicoRepository.findById(cpuId)
                .orElseThrow(() -> new EntityNotFoundException("CPU no encontrado: " + cpuId));

        if (componente.getParent() != null) {
            throw new IllegalStateException("El item ya está asignado a otro CPU");
        }
        if (componente.getEstacion() != null) {
            throw new IllegalStateException("El item está asignado a una estación; libéralo primero");
        }
        if (!esComponenteCpu(componente.getDispositivo())) {
            throw new ValidacionException(
                    "El dispositivo " + componente.getDispositivo().getNombre() + " no es un componente de CPU válido");
        }

        componente.setParent(cpu);
        return toResponseDTO(fisicoRepository.save(componente));
    }

    public InventarioFisicoResponseDTO quitarDeCpu(Long componenteId) {
        InventarioFisico componente = obtenerEntidad(componenteId);
        componente.setParent(null);
        return toResponseDTO(fisicoRepository.save(componente));
    }

    public InventarioFisico obtenerEntidadPorId(Long id) {
        return obtenerEntidad(id);
    }

    public InventarioFisicoResponseDTO crear(InventarioFisicoRequestDTO dto) {
        Estacion estacion = dto.getEstacionId() != null ? validarEstacion(dto.getEstacionId()) : null;
        validarEmpresaCoincide(dto.getEmpresa(), estacion);

        Dispositivo dispositivo = dispositivoService.obtener(dto.getDispositivoId());
        Marca marca = marcaService.obtener(dto.getMarcaId());
        validarMarcaDeDispositivo(marca, dispositivo);

        if (!esComponenteCpu(dispositivo) && (dto.getModelo() == null || dto.getModelo().isBlank())) {
            throw new ValidacionException("El modelo es obligatorio");
        }

        // Los componentes de CPU quedan exentos, igual que con el modelo: su formulario
        // sólo captura dispositivo, marca y especificaciones
        if (!esComponenteCpu(dispositivo) && dispositivo.isRequiereSerie()
                && (dto.getSerie() == null || dto.getSerie().isBlank())) {
            throw new ValidacionException("La serie es obligatoria para " + dispositivo.getNombre());
        }

        if (estacion != null) {
            validarMaxPorEstacion(estacion.getId(), dispositivo);
        }

        InventarioFisico fisico = new InventarioFisico();
        fisico.setEmpresa(dto.getEmpresa());
        fisico.setEstacion(estacion);
        fisico.setDispositivo(dispositivo);
        fisico.setMarca(marca);
        fisico.setModelo(dto.getModelo());
        fisico.setSerie(dto.getSerie());
        fisico.setRamTipo(dto.getRamTipo());
        fisico.setRamEspacio(dto.getRamEspacio());
        fisico.setProcesador(dto.getProcesador());
        fisico.setPulgadas(dto.getPulgadas());
        fisico.setDiscoTipo(dto.getDiscoTipo());
        fisico.setDiscoEspacio(dto.getDiscoEspacio());
        fisico.setHostname(dto.getHostname());
        if (dto.getEstado() != null) fisico.setEstado(dto.getEstado());
        return toResponseDTO(fisicoRepository.save(fisico));
    }

    public InventarioFisicoResponseDTO actualizarParcialmente(Long id, InventarioFisicoUpdateDTO dto) {
        InventarioFisico fisico = obtenerEntidad(id);

        if (dto.getDispositivoId() != null) fisico.setDispositivo(dispositivoService.obtener(dto.getDispositivoId()));
        if (dto.getMarcaId() != null) fisico.setMarca(marcaService.obtener(dto.getMarcaId()));
        if (dto.getModelo() != null) fisico.setModelo(dto.getModelo());
        if (dto.getSerie() != null) fisico.setSerie(dto.getSerie());
        if (dto.getRamTipo() != null) fisico.setRamTipo(dto.getRamTipo());
        if (dto.getRamEspacio() != null) fisico.setRamEspacio(dto.getRamEspacio());
        if (dto.getProcesador() != null) fisico.setProcesador(dto.getProcesador());
        if (dto.getPulgadas() != null) fisico.setPulgadas(dto.getPulgadas());
        if (dto.getDiscoTipo() != null) fisico.setDiscoTipo(dto.getDiscoTipo());
        if (dto.getDiscoEspacio() != null) fisico.setDiscoEspacio(dto.getDiscoEspacio());
        if (dto.getHostname() != null) fisico.setHostname(dto.getHostname());
        if (dto.getEstado() != null) fisico.setEstado(dto.getEstado());
        if (dto.getEstacionId() != null) {
            Estacion estacion = validarEstacion(dto.getEstacionId());
            validarEmpresaCoincide(fisico.getEmpresa(), estacion);
            fisico.setEstacion(estacion);
        }
        if (dto.getEmpresa() != null) {
            validarEmpresaCoincide(dto.getEmpresa(), fisico.getEstacion());
            fisico.setEmpresa(dto.getEmpresa());
        }

        validarMarcaDeDispositivo(fisico.getMarca(), fisico.getDispositivo());

        // Se valida el estado final: cambiar de dispositivo también puede volver la serie obligatoria
        if (!esComponenteCpu(fisico.getDispositivo()) && fisico.getDispositivo().isRequiereSerie()
                && (fisico.getSerie() == null || fisico.getSerie().isBlank())) {
            throw new ValidacionException("La serie es obligatoria para " + fisico.getDispositivo().getNombre());
        }

        return toResponseDTO(fisicoRepository.save(fisico));
    }

    public InventarioFisicoResponseDTO asignarAEstacion(Long itemId, Long nuevaEstacionId) {
        InventarioFisico item = fisicoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item físico no encontrado"));

        if (item.getParent() != null) {
            throw new IllegalStateException("El item es un componente de CPU; libéralo del CPU antes de asignarlo a una estación");
        }

        Estacion nuevaEstacion = estacionRepository.findById(nuevaEstacionId)
                .orElseThrow(() -> new EntityNotFoundException("Estación no encontrada"));

        validarEmpresaCoincide(item.getEmpresa(), nuevaEstacion);

        validarMaxPorEstacion(nuevaEstacionId, item.getDispositivo());

        item.setEstacion(nuevaEstacion);
        return toResponseDTO(fisicoRepository.save(item));
    }

    public InventarioFisicoResponseDTO quitarDeEstacion(Long itemId) {
        InventarioFisico item = fisicoRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item físico no encontrado"));
        item.setEstacion(null);
        return toResponseDTO(fisicoRepository.save(item));
    }

    public void eliminar(Long id) {
        fisicoRepository.delete(obtenerEntidad(id));
    }

    private Estacion validarEstacion(Long estacionId) {
        return estacionRepository.findById(estacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estación no encontrada: " + estacionId));
    }

    private void validarMaxPorEstacion(Long estacionId, Dispositivo dispositivo) {
        int max = MAX_POR_ESTACION.getOrDefault(dispositivo.getNombre().toUpperCase(), 1);
        long actual = fisicoRepository.countByEstacion_IdAndDispositivo_Id(estacionId, dispositivo.getId());
        if (actual >= max) {
            throw new IllegalStateException("La estación ya tiene un item con el dispositivo " + dispositivo.getNombre());
        }
    }

    private void validarMarcaDeDispositivo(Marca marca, Dispositivo dispositivo) {
        if (marca != null && dispositivo != null
                && (marca.getDispositivo() == null
                        || !marca.getDispositivo().getId().equals(dispositivo.getId()))) {
            throw new ValidacionException("La marca no pertenece al dispositivo seleccionado");
        }
    }

    private boolean esComponenteCpu(Dispositivo dispositivo) {
        if (dispositivo.isEsComponenteCpu()) {
            return true;
        }
        String n = dispositivo.getNombre() == null ? "" : dispositivo.getNombre().toUpperCase();
        return n.contains("RAM") || n.contains("MEMORIA")
                || n.contains("DISCO") || n.equals("SSD") || n.equals("HDD") || n.equals("M2")
                || n.contains("PROCESADOR") || n.contains("PROCESSOR");
    }

    private void validarEmpresaCoincide(Empresa empresa, Estacion estacion) {
        if (empresa != null && estacion != null && estacion.getEmpresa() != empresa) {
            throw new IllegalStateException(
                    "La empresa del item (" + empresa + ") no coincide con la empresa de la estación (" + estacion.getEmpresa() + ")");
        }
    }

    private InventarioFisico obtenerEntidad(Long id) {
        return fisicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inventario físico no encontrado: " + id));
    }

    private InventarioFisicoResponseDTO toResponseDTO(InventarioFisico f) {
        Estacion estacion = f.getEstacion();
        Long estacionId = estacion != null ? estacion.getId() : null;
        Long parentId = f.getParent() != null ? f.getParent().getId() : null;
        String parentHostname = f.getParent() != null ? f.getParent().getHostname() : null;
        LocalResponseDTO localDTO = (estacion != null) ? localService.toDTO(estacion.getLocal()) : null;
        return new InventarioFisicoResponseDTO(
                f.getId(),
                f.getEmpresa(),
                estacionId,
                parentId,
                parentHostname,
                dispositivoService.toDTO(f.getDispositivo()),
                marcaService.toDTO(f.getMarca()),
                f.getModelo(),
                f.getSerie(),
                f.getRamTipo(),
                f.getRamEspacio(),
                f.getProcesador(),
                f.getPulgadas(),
                f.getDiscoTipo(),
                f.getDiscoEspacio(),
                f.getHostname(),
                localDTO,
                resolverEstado(f));
    }

    /**
     * Las condiciones almacenadas mandan; si no hay ninguna, el estado sale de la ubicación.
     * Un componente hereda la del CPU que lo aloja, porque no tiene estación propia.
     */
    private EstadoFisico resolverEstado(InventarioFisico f) {
        EstadoFisico almacenado = f.getEstado();
        if (almacenado != null && almacenado.esCondicion()) {
            return almacenado;
        }
        Estacion estacion = f.getEstacion() != null
                ? f.getEstacion()
                : (f.getParent() != null ? f.getParent().getEstacion() : null);
        return estacion != null ? EstadoFisico.OPERATIVO : EstadoFisico.EN_ALMACEN;
    }
}
