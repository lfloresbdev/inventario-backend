package pe.albrugroup.inventario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import pe.albrugroup.inventario_service.dto.LoginRequestDTO;
import pe.albrugroup.inventario_service.exception.DemasiadosIntentosException;
import pe.albrugroup.inventario_service.exception.RecursoNoEncontradoException;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.Rol;
import pe.albrugroup.inventario_service.model.Usuario;
import pe.albrugroup.inventario_service.repository.UsuarioRepository;
import pe.albrugroup.inventario_service.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public record LoginResult(
            String token,
            String nombre,
            String username,
            Rol rol,
            Empresa empresa,
            long expiresAt
    ) {}

    public LoginResult login(LoginRequestDTO dto) {
        String clave = dto.getUsername() == null ? "" : dto.getUsername().trim().toLowerCase();

        if (loginAttemptService.estaBloqueado(clave)) {
            throw new DemasiadosIntentosException(
                    "Demasiados intentos fallidos. Intenta nuevamente en "
                            + loginAttemptService.minutosRestantes(clave) + " minuto(s).");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(clave, dto.getContrasena())
            );
        } catch (AuthenticationException ex) {
            loginAttemptService.registrarFallo(clave);
            throw ex;
        }
        loginAttemptService.registrarExito(clave);

        Usuario usuario = usuarioRepository.findByUsername(clave)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        long expiresAt = System.currentTimeMillis() + jwtUtil.getExpiration();
        String token = jwtUtil.generate(
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.getEmpresa() != null ? usuario.getEmpresa().name() : null
        );
        return new LoginResult(token, usuario.getNombre(), usuario.getUsername(),
                usuario.getRol(), usuario.getEmpresa(), expiresAt);
    }
}
