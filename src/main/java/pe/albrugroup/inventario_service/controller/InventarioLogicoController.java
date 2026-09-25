package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.*;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.TipoAcceso;
import pe.albrugroup.inventario_service.service.InventarioLogicoService;

import java.util.List;

@Tag(name = "inventario-logico")
@RestController
@RequestMapping("/api/inventario-logico")
@RequiredArgsConstructor
public class InventarioLogicoController {

    private final InventarioLogicoService logicoService;

    @GetMapping("/estacion/{estacionId}")
    public ResponseEntity<List<InventarioLogicoResponseDTO>> listarPorEstacion(
            @PathVariable Long estacionId) {
        return ResponseEntity.ok(logicoService.listarPorEstacion(estacionId));
    }

    @GetMapping("/tipo-acceso/{tipoAcceso}")
    public ResponseEntity<List<InventarioLogicoResponseDTO>> listarPorTipoAcceso(
            @PathVariable TipoAcceso tipoAcceso) {
        return ResponseEntity.ok(logicoService.listarPorTipoAcceso(tipoAcceso));
    }

    @GetMapping("/empresa/{empresa}")
    @PreAuthorize("@ilSec.puedeVerEmpresa(#empresa, authentication)")
    public ResponseEntity<List<InventarioLogicoResponseDTO>> listarPorEmpresa(
            @PathVariable Empresa empresa) {
        return ResponseEntity.ok(logicoService.listarPorEmpresa(empresa));
    }

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<InventarioLogicoResponseDTO>> listarPorUbicacion(@PathVariable Long ubicacionId) {
        return ResponseEntity.ok(logicoService.listarPorUbicacion(ubicacionId));
    }

    @GetMapping("/{id}/contrasena")
    @PreAuthorize("@ilSec.puedeVerItem(#id, authentication)")
    public ResponseEntity<ContrasenaResponseDTO> obtenerContrasena(@PathVariable Long id) {
        return ResponseEntity.ok(logicoService.obtenerContrasena(id));
    }

    @PostMapping
    public ResponseEntity<InventarioLogicoResponseDTO> crear(
            @Valid @RequestBody InventarioLogicoRequestDTO dto) {
        InventarioLogicoResponseDTO creado = logicoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InventarioLogicoResponseDTO> actualizarParcialmente(
            @PathVariable Long id,
            @Valid @RequestBody InventarioLogicoUpdateDTO dto) {
        return ResponseEntity.ok(logicoService.actualizarParcialmente(id, dto));
    }

    @PatchMapping("/{id}/asignar-estacion/{estacionId}")
    public ResponseEntity<InventarioLogicoResponseDTO> asignarAEstacion(
            @PathVariable Long id,
            @PathVariable Long estacionId) {
        return ResponseEntity.ok(logicoService.asignarAEstacion(id, estacionId));
    }

    @PatchMapping("/{id}/quitar-estacion")
    public ResponseEntity<InventarioLogicoResponseDTO> quitarDeEstacion(@PathVariable Long id) {
        return ResponseEntity.ok(logicoService.quitarDeEstacion(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}