package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.albrugroup.inventario_service.dto.DispositivoResponseDTO;
import pe.albrugroup.inventario_service.dto.MarcaResponseDTO;
import pe.albrugroup.inventario_service.service.DispositivoService;
import pe.albrugroup.inventario_service.service.MarcaService;

import java.util.List;
import java.util.Map;

@Tag(name = "dispositivos")
@RestController
@RequestMapping("/api/dispositivos")
@RequiredArgsConstructor
public class DispositivoController {

    private final DispositivoService dispositivoService;
    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<DispositivoResponseDTO>> listar() {
        return ResponseEntity.ok(dispositivoService.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DispositivoResponseDTO> crear(@RequestBody Map<String, String> body) {
        boolean esComponenteCpu = Boolean.parseBoolean(body.getOrDefault("esComponenteCpu", "false"));
        boolean requiereSerie = Boolean.parseBoolean(body.getOrDefault("requiereSerie", "true"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dispositivoService.crear(body.get("nombre"), esComponenteCpu, requiereSerie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DispositivoResponseDTO> actualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Boolean esComponenteCpu = body.containsKey("esComponenteCpu")
                ? Boolean.parseBoolean(body.get("esComponenteCpu")) : null;
        Boolean requiereSerie = body.containsKey("requiereSerie")
                ? Boolean.parseBoolean(body.get("requiereSerie")) : null;
        return ResponseEntity.ok(dispositivoService.actualizar(id, body.get("nombre"), esComponenteCpu, requiereSerie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        dispositivoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/marcas")
    public ResponseEntity<List<MarcaResponseDTO>> listarMarcas(@PathVariable Long id) {
        return ResponseEntity.ok(marcaService.listarPorDispositivo(id));
    }

    @PostMapping("/{id}/marcas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarcaResponseDTO> crearMarca(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.crear(id, body.get("nombre")));
    }

    @PutMapping("/{id}/marcas/{marcaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarcaResponseDTO> actualizarMarca(
            @PathVariable Long id,
            @PathVariable Long marcaId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(marcaService.actualizar(id, marcaId, body.get("nombre")));
    }

    @DeleteMapping("/{id}/marcas/{marcaId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarMarca(@PathVariable Long id, @PathVariable Long marcaId) {
        marcaService.eliminar(id, marcaId);
        return ResponseEntity.noContent().build();
    }
}
