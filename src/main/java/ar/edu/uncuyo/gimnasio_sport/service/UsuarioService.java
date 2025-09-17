package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.Rol;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public void crearUsuario(String nombreUsuario, String clave, String confirmacionClave, Rol rol) {
        validar(nombreUsuario, clave, confirmacionClave);

        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse(nombreUsuario))
            throw new BusinessException("El correo electrónico ya está en uso.");

        Usuario usuario = Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .clave(passwordEncoder.encode(clave))
                .rol(rol)
                .build();

        usuarioRepository.save(usuario);
    }

    private void validar(String nombreUsuario, String clave, String confirmacionClave) {
        if (!clave.equals(confirmacionClave))
            throw new BusinessException("Las contraseñas no coinciden.");
    }
}
