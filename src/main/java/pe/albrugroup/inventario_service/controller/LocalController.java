package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.LocalResponseDTO;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.service.LocalService;

import java.util.List;
import java.util.Map;

@Tag(name = "locales")
@RestController
@RequestMapping("/api/locales")
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @GetMapping
    public ResponseEntity<List<LocalResponseDTO>> listar() {
        return ResponseEntity.ok(localService.listar());
    }

    @GetMapping("/empresa/{empresa}")
    public ResponseEntity<List<LocalResponseDTO>> listarPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(localService.listarPorEmpresa(empresa));
    }

    @GetMapping("/inactivos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocalResponseDTO>> listarInactivos() {
        return ResponseEntity.ok(localService.listarInactivos());
    }

    @GetMapping("/empresa/{empresa}/inactivos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LocalResponseDTO>> listarInactivosPorEmpresa(@PathVariable Empresa empresa) {
        return ResponseEntity.ok(localService.listarInactivosPorEmpresa(empresa));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponseDTO> crear(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        Empresa empresa = Empresa.valueOf(body.get("empresa"));
        return ResponseEntity.status(HttpStatus.CREATED).body(localService.crear(nombre, empresa));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponseDTO> actualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        Empresa empresa = Empresa.valueOf(body.get("empresa"));
        return ResponseEntity.ok(localService.actualizar(id, nombre, empresa));
    }

    @PatchMapping("/{id}/dar-de-baja")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponseDTO> darDeBaja(@PathVariable Long id) {
        return ResponseEntity.ok(localService.darDeBaja(id));
    }

    @PatchMapping("/{id}/reactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocalResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(localService.reactivar(id));
    }
}
