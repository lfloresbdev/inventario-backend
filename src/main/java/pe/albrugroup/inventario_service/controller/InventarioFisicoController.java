package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.InventarioFisicoRequestDTO;
import pe.albrugroup.inventario_service.dto.InventarioFisicoResponseDTO;
import pe.albrugroup.inventario_service.dto.InventarioFisicoUpdateDTO;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.service.InventarioFisicoService;

import java.util.List;

@Tag(name = "inventario-fisico")
@RestController
@RequestMapping("/api/inventario-fisico")
@RequiredArgsConstructor
public class InventarioFisicoController {

    private final InventarioFisicoService fisicoService;

    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarPorEstacion(@PathVariable Long estacionId) {
        return ResponseEntity.ok(fisicoService.listarPorEstacion(estacionId));
    }

    @GetMapping("/dispositivo/{dispositivoId}")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarPorDispositivo(@PathVariable Long dispositivoId) {
        return ResponseEntity.ok(fisicoService.listarPorDispositivo(dispositivoId));
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarPorLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(fisicoService.listarPorLocal(localId));
    }

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarPorUbicacion(@PathVariable Long ubicacionId) {
        return ResponseEntity.ok(fisicoService.listarPorUbicacion(ubicacionId));
    }

    @GetMapping("/empresa/{empresa}")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(fisicoService.listarPorEmpresa(empresa));
    }

    @GetMapping("/empresa/{empresa}/sin-asignar")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarSinAsignarPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(fisicoService.listarSinAsignarPorEmpresa(empresa));
    }

    @PostMapping
    public ResponseEntity<InventarioFisicoResponseDTO> crear(@Valid @RequestBody InventarioFisicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fisicoService.crear(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InventarioFisicoResponseDTO> actualizarParcialmente(
            @PathVariable Long id,
            @RequestBody InventarioFisicoUpdateDTO dto) {
        return ResponseEntity.ok(fisicoService.actualizarParcialmente(id, dto));
    }

    @PatchMapping("/{id}/asignar-estacion/{estacionId}")
    public ResponseEntity<InventarioFisicoResponseDTO> asignarAEstacion(
            @PathVariable Long id,
            @PathVariable Long estacionId) {
        return ResponseEntity.ok(fisicoService.asignarAEstacion(id, estacionId));
    }

    @PatchMapping("/{id}/quitar-estacion")
    public ResponseEntity<InventarioFisicoResponseDTO> quitarDeEstacion(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.quitarDeEstacion(id));
    }

    @GetMapping("/{id}/componentes")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarComponentes(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.listarComponentes(id));
    }

    @GetMapping("/empresa/{empresa}/libres")
    public ResponseEntity<List<InventarioFisicoResponseDTO>> listarLibresPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(fisicoService.listarLibresPorEmpresa(empresa));
    }

    @PatchMapping("/{id}/asignar-cpu/{cpuId}")
    public ResponseEntity<InventarioFisicoResponseDTO> asignarACpu(
            @PathVariable Long id,
            @PathVariable Long cpuId) {
        return ResponseEntity.ok(fisicoService.asignarACpu(id, cpuId));
    }

    @PatchMapping("/{id}/quitar-cpu")
    public ResponseEntity<InventarioFisicoResponseDTO> quitarDeCpu(@PathVariable Long id) {
        return ResponseEntity.ok(fisicoService.quitarDeCpu(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        fisicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
