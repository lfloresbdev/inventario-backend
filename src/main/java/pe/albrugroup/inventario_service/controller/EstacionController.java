package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.EstacionCreateDTO;
import pe.albrugroup.inventario_service.dto.EstacionUpdateDTO;
import pe.albrugroup.inventario_service.dto.EstacionResponseDTO;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.service.EstacionService;

import java.util.List;

@Tag(name = "estaciones")
@RestController
@RequestMapping("/api/estaciones")
@RequiredArgsConstructor
public class EstacionController {

    private final EstacionService estacionService;

    @GetMapping
    public ResponseEntity<List<EstacionResponseDTO>> listar() {
        return ResponseEntity.ok(estacionService.listar());
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<List<EstacionResponseDTO>> listarPorLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(estacionService.listarPorLocal(localId));
    }

    @GetMapping("/empresa/{empresa}")
    public ResponseEntity<List<EstacionResponseDTO>> listarPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(estacionService.listarPorEmpresa(empresa));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstacionResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estacionService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EstacionResponseDTO> crear(@Valid @RequestBody EstacionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estacionService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstacionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstacionUpdateDTO dto) {
        return ResponseEntity.ok(estacionService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inactivos")
    public ResponseEntity<List<EstacionResponseDTO>> listarInactivos() {
        return ResponseEntity.ok(estacionService.listarInactivos());
    }

    @PatchMapping("/{id}/dar-de-baja")
    public ResponseEntity<EstacionResponseDTO> darDeBaja(@PathVariable Long id) {
        return ResponseEntity.ok(estacionService.darDeBaja(id));
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<EstacionResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(estacionService.reactivar(id));
    }
}
