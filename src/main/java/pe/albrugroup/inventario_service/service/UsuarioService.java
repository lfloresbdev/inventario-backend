package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.UsuarioRequestDTO;
import pe.albrugroup.inventario_service.dto.UsuarioResponseDTO;
import pe.albrugroup.inventario_service.dto.UsuarioUpdateDTO;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.exception.RecursoDuplicadoException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.model.Usuario;
import pe.albrugroup.inventario_service.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<UsuarioResponseDTO> listarPorEmpresa(Empresa empresa) {
        return usuarioRepository.findByEmpresa(empresa).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return toResponseDTO(obtenerEntidad(id));
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        validarUsernameDisponible(dto.getUsername(), null);
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setUsername(dto.getUsername());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setEmpresa(dto.getEmpresa());
        usuario.setRol(dto.getRol());
        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = obtenerEntidad(id);

        if (dto.getUsername() != null) {
            validarUsernameDisponible(dto.getUsername(), id);
            usuario.setUsername(dto.getUsername());
        }
        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getContrasena() != null) usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        if (dto.getEmpresa() != null) usuario.setEmpresa(dto.getEmpresa());
        if (dto.getRol() != null) usuario.setRol(dto.getRol());

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    public void eliminar(Long id) {
        usuarioRepository.delete(obtenerEntidad(id));
    }

    private void validarUsernameDisponible(String username, Long id) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(username);
        if (existente.isPresent() && !existente.get().getId().equals(id)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con username '" + username + "'");
        }
    }

    private Usuario obtenerEntidad(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getUsername(),
                usuario.getEmpresa(),
                usuario.getRol(),
                usuario.getCreadoEn());
    }
}
