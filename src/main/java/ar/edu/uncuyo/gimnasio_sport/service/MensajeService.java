package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.repository.MensajeRepository;
import ar.edu.uncuyo.gimnasio_sport.model.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.model.Usuario;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<Mensaje> listar(FiltroMensajeDTO filtro, Pageable pageable) {
        TipoMensaje tipo = filtro == null ? null : filtro.getTipoMensaje();
        String titulo = (filtro == null || filtro.getTituloContiene() == null || filtro.getTituloContiene().isBlank()) ? null : filtro.getTituloContiene().trim();
        String nombreUsuario = (filtro == null || filtro.getNombreUsuario() == null || filtro.getNombreUsuario().isBlank()) ? null : filtro.getNombreUsuario().trim();
        return mensajeRepository.filtrar(tipo, titulo, nombreUsuario, pageable);
    }

    @Transactional(readOnly = true)
    public Mensaje obtener(String id) {
        return mensajeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));
    }

    @Transactional
    public Mensaje crear(MensajeDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
        Mensaje m = new Mensaje();
        m.setTitulo(dto.getTitulo());
        m.setTexto(dto.getTexto());
        m.setTipoMensaje(dto.getTipoMensaje());
        m.setUsuario(usuario);
        m.setEliminado(false);
        return mensajeRepository.save(m);
    }

    @Transactional
    public Mensaje actualizar(String id, MensajeDTO dto) {
        Mensaje m = obtener(id);
        m.setTitulo(dto.getTitulo());
        m.setTexto(dto.getTexto());
        m.setTipoMensaje(dto.getTipoMensaje());
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
            m.setUsuario(usuario);
        }
        return mensajeRepository.save(m);
    }

    @Transactional
    public void eliminarLogico(String id) {
        Mensaje m = obtener(id);
        m.setEliminado(true);
        mensajeRepository.save(m);
    }
}

