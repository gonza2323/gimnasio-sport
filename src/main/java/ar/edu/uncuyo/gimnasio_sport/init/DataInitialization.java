package ar.edu.uncuyo.gimnasio_sport.init;

import ar.edu.uncuyo.gimnasio_sport.enums.Rol;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse("admin")) {
            System.out.println("Datos iniciales ya creados. Salteando creación de datos iniciales. Para forzar su creación, borrar la base de datos");
            return;
        }
        System.out.println("Creando datos iniciales...");

        usuarioService.crearUsuario("admin","1234", "1234", Rol.ADMIN);
    }
}
