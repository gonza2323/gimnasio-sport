package ar.edu.uncuyo.gimnasio_sport.init;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.dto.UsuarioCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.PaisService;
import ar.edu.uncuyo.gimnasio_sport.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PaisService paisService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse("admin")) {
            System.out.println("Datos iniciales ya creados. Salteando creación de datos iniciales. Para forzar su creación, borrar la base de datos");
            return;
        }

        // 1. Create Authentication manually
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRA"));
        var auth = new UsernamePasswordAuthenticationToken("system", null, authorities);

        // 2. Set it in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("Creando datos iniciales...");

        UsuarioCreateFormDTO form = new UsuarioCreateFormDTO(null, "admin", "1234", "1234", RolUsuario.ADMINISTRATIVO);
        usuarioService.crearUsuario(form);

        crearUbicaciones();
        SecurityContextHolder.clearContext();
    }

    public void crearUbicaciones() {
        PaisDto paisDto1 = new PaisDto(null, "Argentina");
        PaisDto paisDto2 = new PaisDto(null, "España");

        paisService.crearPais(paisDto1);
        paisService.crearPais(paisDto2);
    }
}
