package ar.edu.uncuyo.gimnasio_sport.jobs;

import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import ar.edu.uncuyo.gimnasio_sport.service.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class BirthdayMessageJob {

    private final SocioService socioService;
    private final MensajeService mensajeService;

    @Value("${app.mensajes.cumple.enabled:true}")
    private boolean enabled;

    @Value("${app.mensajes.cumple.zone:America/Argentina/Buenos_Aires}")
    private String zoneId;

    @Value("${app.mensajes.cumple.titulo:¡Feliz cumpleaños!}")
    private String titulo;

    @Value("${app.mensajes.cumple.texto:Desde Gimnasio Sport te deseamos un gran día.}")
    private String texto;

    @Scheduled(cron = "${app.mensajes.cumple.cron:0 9 0 30 * *}", zone = "${app.mensajes.cumple.zone:America/Argentina/Buenos_Aires}")
    @Transactional
    public void enviarFelicitaciones() {
        if (!enabled) {
            return;
        }

        LocalDate hoy = LocalDate.now(ZoneId.of(zoneId));
        List<Socio> socios = socioService.ListarSocios().stream().filter(socio -> socio.getFechaNacimiento().getDayOfMonth() == hoy.getDayOfMonth()).toList();

        for (Socio socio : socios) {
            if (socio.getFechaNacimiento() == null) {
                continue;
            }
            if (socio.getUsuario() == null || socio.getUsuario().getId() == null) {
                continue;
            }

            LocalDate nacimiento = socio.getFechaNacimiento();
            if (nacimiento.getMonth() == hoy.getMonth() && nacimiento.getDayOfMonth() == hoy.getDayOfMonth()) {
                MensajeDTO dto = new MensajeDTO();
                String nombreCompleto = formatearNombreCompleto(socio.getNombre(), socio.getApellido());
                if (!StringUtils.hasText(nombreCompleto)) {
                    nombreCompleto = socio.getNombre();
                }
                dto.setNombre(nombreCompleto);
                dto.setEmail(socio.getCorreoElectronico());
                dto.setAsunto(titulo);
                dto.setCuerpo(texto);
                dto.setTipo(TipoMensaje.CUMPLEANOS);
                dto.setFechaProgramada(LocalDateTime.now(ZoneId.of(zoneId)));
                dto.setUsuarioId(socio.getUsuario().getId());
                mensajeService.crear(dto);
            }
        }
    }

    private String formatearNombreCompleto(String nombre, String apellido) {
        return Stream.of(nombre, apellido)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
