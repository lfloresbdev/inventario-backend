package pe.albrugroup.inventario_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.albrugroup.inventario_service.dto.LoginRequestDTO;
import pe.albrugroup.inventario_service.dto.LoginResponseDTO;
import pe.albrugroup.inventario_service.security.JwtUtil;
import pe.albrugroup.inventario_service.service.AuthService;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto,
                                                  HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(dto);
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie(result.token()).toString());
        return ResponseEntity.ok(new LoginResponseDTO(
                result.nombre(), result.username(), result.rol(), result.empresa(), result.expiresAt(), result.token()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, authCookie("").maxAge(0).build().toString());
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie.ResponseCookieBuilder authCookie(String value) {
        return ResponseCookie.from("auth_token", value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path("/api")
                .maxAge(Duration.ofMillis(jwtUtil.getExpiration()));
    }
}
