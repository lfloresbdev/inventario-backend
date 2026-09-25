package pe.albrugroup.inventario_service.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_INTENTOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);

    private record Intentos(int cantidad, Instant ultimoIntento, Instant bloqueadoHasta) {}

    private final Map<String, Intentos> intentos = new ConcurrentHashMap<>();

    public boolean estaBloqueado(String clave) {
        Intentos actual = intentos.get(clave);
        if (actual == null) {
            return false;
        }
        Instant ahora = Instant.now();
        if (actual.bloqueadoHasta() != null) {
            if (ahora.isAfter(actual.bloqueadoHasta())) {
                intentos.remove(clave);
                return false;
            }
            return true;
        }
        if (transcurrioVentana(actual, ahora)) {
            intentos.remove(clave);
        }
        return false;
    }

    public long minutosRestantes(String clave) {
        Intentos actual = intentos.get(clave);
        if (actual == null || actual.bloqueadoHasta() == null) {
            return 0;
        }
        long segundos = Duration.between(Instant.now(), actual.bloqueadoHasta()).getSeconds();
        return Math.max(1, (segundos + 59) / 60);
    }

    public void registrarFallo(String clave) {
        Instant ahora = Instant.now();
        intentos.compute(clave, (k, actual) -> {
            int cantidad = 1;
            if (actual != null && actual.bloqueadoHasta() == null && !transcurrioVentana(actual, ahora)) {
                cantidad = actual.cantidad() + 1;
            }
            if (cantidad >= MAX_INTENTOS) {
                return new Intentos(cantidad, ahora, ahora.plus(BLOQUEO));
            }
            return new Intentos(cantidad, ahora, null);
        });
    }

    public void registrarExito(String clave) {
        intentos.remove(clave);
    }

    private boolean transcurrioVentana(Intentos actual, Instant ahora) {
        return actual.ultimoIntento() != null
                && Duration.between(actual.ultimoIntento(), ahora).compareTo(BLOQUEO) > 0;
    }
}
