package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.UbicacionFisicaResponseDTO;
import pe.albrugroup.inventario_service.service.UbicacionFisicaService;

import java.util.List;
import java.util.Map;

@Tag(name = "ubicaciones-fisicas")
@RestController
@RequestMapping("/api/ubicaciones-fisicas")
@RequiredArgsConstructor
public class UbicacionFisicaController {

    private final UbicacionFisicaService ubicacionService;

    @GetMapping
    public ResponseEntity<List<UbicacionFisicaResponseDTO>> listar() {
        return ResponseEntity.ok(ubicacionService.listar());
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<List<UbicacionFisicaResponseDTO>> listarPorLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(ubicacionService.listarPorLocal(localId));
    }

    @GetMapping("/inactivos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UbicacionFisicaResponseDTO>> listarInactivos() {
        return ResponseEntity.ok(ubicacionService.listarInactivos());
    }

    @PatchMapping("/{id}/dar-de-baja")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UbicacionFisicaResponseDTO> darDeBaja(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.darDeBaja(id));
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UbicacionFisicaResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.reactivar(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UbicacionFisicaResponseDTO> crear(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        Long localId = ((Number) body.get("localId")).longValue();
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionService.crear(nombre, localId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UbicacionFisicaResponseDTO> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        Long localId = ((Number) body.get("localId")).longValue();
        return ResponseEntity.ok(ubicacionService.actualizar(id, nombre, localId));
    }
}
