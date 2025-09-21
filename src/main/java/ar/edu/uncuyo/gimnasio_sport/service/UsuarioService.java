package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.UsuarioCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.UsuarioMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public Usuario crearUsuario(UsuarioCreateFormDTO formDto) {
        validarDatos(formDto.getClave(), formDto.getConfirmacionClave());

        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse(formDto.getNombreUsuario()))
            throw new BusinessException("YaExiste.usuario.nombre");

        Usuario usuario = usuarioMapper.toEntity(formDto);
        String hashClave = passwordEncoder.encode(formDto.getClave());
        usuario.setClave(hashClave);

        return usuarioRepository.save(usuario);
    }

    private void validarDatos(String clave, String confirmacionClave) {
        if (!clave.equals(confirmacionClave))
            throw new BusinessException("NoIguales.usuario.confirmacionClave");
    }
}
