package pe.albrugroup.inventario_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.albrugroup.inventario_service.enums.Empresa;
import pe.albrugroup.inventario_service.enums.Rol;
import pe.albrugroup.inventario_service.model.*;
import pe.albrugroup.inventario_service.repository.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalRepository localRepository;
    private final UbicacionFisicaRepository ubicacionFisicaRepository;
    private final DispositivoRepository dispositivoRepository;
    private final MarcaRepository marcaRepository;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Value("${app.seed.user-password}")
    private String userPassword;

    private static final java.util.Set<String> COMPONENTES_CPU = java.util.Set.of("RAM", "PROCESADOR", "DISCO");

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedLocales();
        seedDispositivos();
        seedMarcasPorDispositivo();
    }

    private void seedUsuarios() {
        List<SeedUser> seeds = List.of(
                new SeedUser("Administrador",  "admin",       adminPassword, null,          Rol.ADMIN),
                new SeedUser("Usuario Lybtel", "user.lybtel", userPassword,  Empresa.LYBTEL, Rol.USER),
                new SeedUser("Usuario Runa",   "user.runa",   userPassword,  Empresa.RUNA,   Rol.USER)
        );
        for (SeedUser s : seeds) {
            if (usuarioRepository.existsByUsername(s.username())) continue;
            Usuario u = new Usuario();
            u.setNombre(s.nombre());
            u.setUsername(s.username());
            u.setContrasena(passwordEncoder.encode(s.password()));
            u.setEmpresa(s.empresa());
            u.setRol(s.rol());
            usuarioRepository.save(u);
            log.info("Seed: usuario '{}' creado ({} / {})", s.username(), s.empresa(), s.rol());
        }
    }

    private void seedLocales() {
        List<SeedLocal> locales = List.of(
                new SeedLocal("PALMERAS", Empresa.LYBTEL),
                new SeedLocal("ARIES",    Empresa.RUNA)
        );
        for (SeedLocal s : locales) {
            if (localRepository.existsByNombreAndEmpresaAndActivoTrue(s.nombre(), s.empresa())) continue;
            Local local = new Local(null, s.nombre(), s.empresa(), true);
            Local saved = localRepository.save(local);
            log.info("Seed: local '{}' ({}) creado", saved.getNombre(), saved.getEmpresa());
            seedUbicaciones(saved);
        }
    }

    private void seedUbicaciones(Local local) {
        List<String> nombres = List.of("ÁREA PRINCIPAL", "DEPÓSITO", "RECEPCIÓN");
        for (String nombre : nombres) {
            if (ubicacionFisicaRepository.existsByNombreAndLocalId(nombre, local.getId())) continue;
            UbicacionFisica ub = new UbicacionFisica(null, nombre, local, true);
            ubicacionFisicaRepository.save(ub);
            log.info("Seed: ubicación '{}' en local '{}' creada", nombre, local.getNombre());
        }
    }

    private void seedDispositivos() {
        List<String> nombres = List.of(
                "CPU", "MONITOR", "LAPTOP", "AUDÍFONOS", "MOUSE", "TECLADO",
                "CABLE VGA", "CABLE PODER", "CABLE HDMI", "CARGADOR",
                "CELULAR", "BASE COOLER", "CÁMARA", "TV", "PROYECTOR",
                "REFRIGERADOR", "MICROONDAS", "CARGADOR DE LAPTOP",
                "RAM", "PROCESADOR", "DISCO"
        );
        for (String nombre : nombres) {
            boolean esComponente = COMPONENTES_CPU.contains(nombre);
            Dispositivo existente = dispositivoRepository.findByNombre(nombre).orElse(null);
            if (existente != null) {
                if (esComponente && !existente.isEsComponenteCpu()) {
                    existente.setEsComponenteCpu(true);
                    dispositivoRepository.save(existente);
                    log.info("Seed: dispositivo '{}' marcado como componente de CPU", nombre);
                }
                continue;
            }
            Dispositivo d = new Dispositivo();
            d.setNombre(nombre);
            d.setEsComponenteCpu(esComponente);
            dispositivoRepository.save(d);
            log.info("Seed: dispositivo '{}' creado{}", nombre, esComponente ? " (componente CPU)" : "");
        }
    }

    private void seedMarcasPorDispositivo() {
        Map<String, List<String>> marcasPorDispositivo = Map.ofEntries(
                Map.entry("CPU", List.of("DELL", "HP", "LENOVO")),
                Map.entry("MONITOR", List.of("ADVANCE", "DELL", "HP", "LENOVO", "LG", "SAMSUNG", "TEROS")),
                Map.entry("LAPTOP", List.of("LENOVO")),
                Map.entry("AUDÍFONOS", List.of("LOGITECH", "MICRONICS", "TEROS")),
                Map.entry("MOUSE", List.of("DELL", "HP", "LENOVO", "LOGITECH", "MICRONICS")),
                Map.entry("TECLADO", List.of("DELL", "ENKORE", "HOCHI", "HP", "LENOVO", "LOGITECH", "MICRONICS", "SAMSUNG")),
                Map.entry("CABLE VGA", List.of("GENÉRICO")),
                Map.entry("CABLE PODER", List.of("GENÉRICO")),
                Map.entry("CABLE HDMI", List.of("GENÉRICO")),
                Map.entry("CARGADOR", List.of("REDMI")),
                Map.entry("CELULAR", List.of("REDMI")),
                Map.entry("BASE COOLER", List.of("AIRBCOM")),
                Map.entry("CÁMARA", List.of("MICRONICS")),
                Map.entry("TV", List.of("TCL", "JVC")),
                Map.entry("PROYECTOR", List.of("VIEWSONIC")),
                Map.entry("REFRIGERADOR", List.of("MIRAY")),
                Map.entry("MICROONDAS", List.of("OSTER")),
                Map.entry("CARGADOR DE LAPTOP", List.of("LENOVO")),
                Map.entry("RAM", List.of("CRUCIAL", "HIKSEMI", "RAMAXEL", "SAMSUNG")),
                Map.entry("PROCESADOR", List.of("AMD", "INTEL")),
                Map.entry("DISCO", List.of("KINGSTON", "HIKSEMI", "SAMSUNG", "CASI", "WD", "LENOVO"))
        );
        marcasPorDispositivo.forEach((nombreDispositivo, nombres) -> {
            Dispositivo dispositivo = dispositivoRepository.findByNombre(nombreDispositivo).orElse(null);
            if (dispositivo == null) return;
            for (String nombreMarca : nombres) {
                if (marcaRepository.existsByNombreAndDispositivo_Id(nombreMarca, dispositivo.getId())) continue;
                Marca m = new Marca();
                m.setNombre(nombreMarca);
                m.setDispositivo(dispositivo);
                marcaRepository.save(m);
                log.info("Seed: marca '{}' creada para '{}'", nombreMarca, nombreDispositivo);
            }
        });
    }

    private record SeedUser(String nombre, String username, String password, Empresa empresa, Rol rol) {}
    private record SeedLocal(String nombre, Empresa empresa) {}
}
