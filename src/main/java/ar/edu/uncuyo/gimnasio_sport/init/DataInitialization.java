package ar.edu.uncuyo.gimnasio_sport.init;

import ar.edu.uncuyo.gimnasio_sport.enums.Rol;
import ar.edu.uncuyo.gimnasio_sport.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Creando datos iniciales...");

        usuarioService.crearUsuario("admin","1234", "1234", Rol.ADMIN);
    }
}
