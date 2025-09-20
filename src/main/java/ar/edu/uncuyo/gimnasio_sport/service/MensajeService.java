package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.mapper.MensajeMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.MensajeRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensajeMapper mensajeMapper;

    @Transactional(readOnly = true)
    public Page<Mensaje> listar(FiltroMensajeDTO filtro, Pageable pageable) {
        TipoMensaje tipo = filtro == null ? null : filtro.getTipoMensaje();
        String titulo = limpiarCadena(filtro != null ? filtro.getTituloContiene() : null);
        String nombreUsuario = limpiarCadena(filtro != null ? filtro.getNombreUsuario() : null);
        return mensajeRepository.filtrar(tipo, titulo, nombreUsuario, pageable);
    }

    @Transactional(readOnly = true)
    public Mensaje obtener(Long id) {
        return mensajeRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));
    }

    @Transactional
    public Mensaje crear(MensajeDTO dto) {
        validarMensaje(dto);
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
        Mensaje m = mensajeMapper.toEntity(dto);
        m.setUsuario(usuario);
        m.setEliminado(false);
        normalizarCamposMensaje(m);
        return mensajeRepository.save(m);
    }

    @Transactional
    public Mensaje actualizar(Long id, MensajeDTO dto) {
        validarMensaje(dto);
        Mensaje m = obtener(id);
        mensajeMapper.updateEntityFromDto(dto, m);
        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(m.getUsuario().getId())) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
            m.setUsuario(usuario);
        }
        normalizarCamposMensaje(m);
        return mensajeRepository.save(m);
    }

    @Transactional(readOnly = true)
    public MensajeDTO toDto(Mensaje mensaje) {
        return mensajeMapper.toDto(mensaje);
    }

    @Transactional
    public void eliminarLogico(Long id) {
        Mensaje m = obtener(id);
        m.setEliminado(true);
        mensajeRepository.save(m);
    }

    private void validarMensaje(MensajeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El mensaje no puede ser nulo");
        }
        if (!StringUtils.hasText(dto.getTitulo())) {
            throw new IllegalArgumentException("El título del mensaje es obligatorio");
        }
        if (!StringUtils.hasText(dto.getTexto())) {
            throw new IllegalArgumentException("El texto del mensaje es obligatorio");
        }
        if (dto.getTipoMensaje() == null) {
            throw new IllegalArgumentException("El tipo de mensaje es obligatorio");
        }
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("Debe indicarse el usuario asociado al mensaje");
        }
    }

    private void normalizarCamposMensaje(Mensaje mensaje) {
        if (mensaje == null) {
            return;
        }
        mensaje.setTitulo(normalizarCadena(mensaje.getTitulo()));
        mensaje.setTexto(normalizarCadena(mensaje.getTexto()));
    }

    private String limpiarCadena(String valor) {
        if (!StringUtils.hasText(valor)) {
            return null;
        }
        return valor.trim();
    }

    private String normalizarCadena(String valor) {
        return valor == null ? null : valor.trim();
    }
}
