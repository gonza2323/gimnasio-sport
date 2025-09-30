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
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensajeMapper mensajeMapper;
    private final JavaMailSender mailSender;
    private final PromotionSchedulerService promotionSchedulerService;
    private final TaskScheduler taskScheduler;

    private static final String REMITENTE = "gimnasiosport21@gmail.com";

    @Transactional(readOnly = true)
    public List<Mensaje> listar(FiltroMensajeDTO filtro) {
        List<Mensaje> mensajes = mensajeRepository.findAllByEliminadoFalse();
        if (filtro == null) {
            return mensajes;
        }
        return mensajes.stream()
                .filter(m -> filtro.getTipoMensaje() == null || filtro.getTipoMensaje() == m.getTipo())
                .filter(m -> !StringUtils.hasText(filtro.getAsuntoContiene()) ||
                        (m.getAsunto() != null && m.getAsunto().toLowerCase().contains(filtro.getAsuntoContiene().toLowerCase())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Mensaje obtener(Long id) {
        return mensajeRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Mensaje no encontrado"));
    }

    @Transactional
    public Mensaje crear(MensajeDTO dto) {
        normalizarDto(dto);
        validarMensaje(dto);
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
        Mensaje m = mensajeMapper.toEntity(dto);
        m.setUsuario(usuario);
        m.setEliminado(false);
        normalizarCamposMensaje(m);
        Mensaje guardado = mensajeRepository.save(m);
        enviarCorreo(dto);
        return guardado;
    }

    @Transactional
    public Mensaje actualizar(Long id, MensajeDTO dto) {
        normalizarDto(dto);
        validarMensaje(dto);
        Mensaje m = obtener(id);
        mensajeMapper.updateEntityFromDto(dto, m);
        if (dto.getUsuarioId() != null && !dto.getUsuarioId().equals(m.getUsuario().getId())) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario asociado no encontrado"));
            m.setUsuario(usuario);
        }
        normalizarCamposMensaje(m);
        Mensaje guardado = mensajeRepository.save(m);
        enviarCorreo(dto);
        return guardado;
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

        if (dto.getTipo() == TipoMensaje.PROMOCION) {
            if (dto.getUsuarioId() == null) {
                throw new IllegalArgumentException("Debe indicarse el usuario asociado al mensaje");
            }
            // Para promoción no se valida ni nombre ni email
            return;
        }

        // Caso normal
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico del destinatario es obligatorio");
        }
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("Debe indicarse el usuario asociado al mensaje");
        }
    }


    private void normalizarDto(MensajeDTO dto) {
        if (dto == null) {
            return;
        }

        // Si es una promocion, NO rellenamos nombre ni email
        if (dto.getTipo() == TipoMensaje.PROMOCION) {
            if (!StringUtils.hasText(dto.getAsunto())) {
                dto.setAsunto("Promoción Gimnasio Sport");
            }
            if (!StringUtils.hasText(dto.getCuerpo())) {
                dto.setCuerpo("¡Tenemos una promoción especial para vos!");
            }
            return;
        }

        if (!StringUtils.hasText(dto.getNombre())) {
            dto.setNombre("Destinatario");
        }
        if (!StringUtils.hasText(dto.getAsunto())) {
            dto.setAsunto("Mensaje Gimnasio Sport");
        }
        if (!StringUtils.hasText(dto.getCuerpo())) {
            dto.setCuerpo("Hola! Queríamos compartir una novedad contigo.");
        }
        if (dto.getTipo() == null) {
            dto.setTipo(TipoMensaje.OTROS);
        }
    }


    public void enviarCorreo(MensajeDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getTipo() == TipoMensaje.PROMOCION) {
            if (dto.getFechaProgramada() != null) {
                // 👉 Programar para la fecha indicada
                ZonedDateTime fechaEjecucion = dto.getFechaProgramada().atZone(ZoneId.systemDefault());
                promotionSchedulerService.programarPromocion(dto.getAsunto(), dto.getCuerpo(), fechaEjecucion);
            } else {
                // 👉 Envío inmediato, asíncrono
                promotionSchedulerService.enviarPromocion(dto.getAsunto(), dto.getCuerpo());
            }
        } else {
            enviarCorreoIndividual(dto);
        }
    }



    public void enviarCorreoIndividual(MensajeDTO dto) {
        if (!StringUtils.hasText(dto.getEmail())) {
            return;
        }

        Runnable tarea = () -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(REMITENTE);
                message.setTo(dto.getEmail());
                message.setSubject(
                        StringUtils.hasText(dto.getAsunto()) ? dto.getAsunto() : "Mensaje Gimnasio Sport"
                );
                message.setText(
                        StringUtils.hasText(dto.getCuerpo()) ? dto.getCuerpo() : "Hola!"
                );
                mailSender.send(message);
            } catch (Exception ex) {
                log.warn("No se pudo enviar el correo a {}", dto.getEmail(), ex);
            }
        };

        if (dto.getFechaProgramada() != null) {
            Instant instante = dto.getFechaProgramada()
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            taskScheduler.schedule(tarea, Date.from(instante));
        } else {
            taskScheduler.schedule(tarea, new Date()); // 🔹 siempre asíncrono
        }
    }

    private void normalizarCamposMensaje(Mensaje mensaje) {
        if (mensaje == null) {
            return;
        }
        mensaje.setNombre(normalizarCadena(mensaje.getNombre()));
        mensaje.setEmail(normalizarCadena(mensaje.getEmail()));
        mensaje.setAsunto(normalizarCadena(mensaje.getAsunto()));
        mensaje.setCuerpo(normalizarCadena(mensaje.getCuerpo()));
    }

    private String normalizarCadena(String valor) {
        return valor == null ? null : valor.trim();
    }
}
