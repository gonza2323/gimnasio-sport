package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PromotionSchedulerService {

    private final TaskScheduler taskScheduler;
    private final SocioRepository socioRepository;
    private final MensajeService mensajeService;

    public ScheduledFuture<?> programarPromocion(String asunto, String cuerpo, ZonedDateTime fechaEjecucion) {
        Runnable envio = () -> enviarPromocion(asunto, cuerpo);
        return taskScheduler.schedule(envio, Date.from(fechaEjecucion.toInstant()));
    }

    void enviarPromocion(String asunto, String cuerpo) {
        List<Socio> socios = socioRepository.findAll();
        for (Socio socio : socios) {
            if (socio.getUsuario() == null || socio.getUsuario().getId() == null) {
                continue;
            }
            String nombreCompleto = Stream.of(socio.getNombre(), socio.getApellido())
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(part -> !part.isEmpty())
                    .collect(Collectors.joining(" "));

            if (!StringUtils.hasText(nombreCompleto)) {
                nombreCompleto = "Socio Gimnasio";
            }

            MensajeDTO dto = new MensajeDTO(
                    null,
                    nombreCompleto,
                    socio.getCorreoElectronico(),
                    asunto,
                    cuerpo,
                    TipoMensaje.PROMOCION,
                    LocalDateTime.now(),
                    socio.getUsuario().getId()
            );
            mensajeService.crear(dto);
        }
    }


}

