package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PromotionSchedulerService {

    private final TaskScheduler taskScheduler;
    private final SocioService socioService;
    private final JavaMailSender mailSender;

    private static final String REMITENTE = "gimnasiosport21@gmail.com";

    public ScheduledFuture<?> programarPromocion(String asunto, String cuerpo, ZonedDateTime fechaEjecucion) {
        Runnable envio = () -> enviarPromocion(asunto, cuerpo);
        return taskScheduler.schedule(envio, Date.from(fechaEjecucion.toInstant()));
    }

    @Async
    public void enviarPromocion(String asunto, String cuerpo) {
        List<Socio> socios = socioService.listarSocios();

        for (Socio socio : socios) {
            if (!StringUtils.hasText(socio.getCorreoElectronico())) {
                continue;
            }

            Runnable tarea = () -> {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(REMITENTE);
                    message.setTo(socio.getCorreoElectronico());
                    message.setSubject(StringUtils.hasText(asunto) ? asunto : "Promoción Gimnasio Sport");
                    message.setText(StringUtils.hasText(cuerpo) ? cuerpo : "¡Tenemos una promoción especial para vos!");
                    mailSender.send(message);
                } catch (Exception ex) {
                    // Podés loguear, o guardar en tabla de errores de envío
                    System.err.println("No se pudo enviar correo de promoción a " + socio.getCorreoElectronico() + ": " + ex.getMessage());
                }
            };

            // Si quisieras, acá podés también programar diferido con taskScheduler
            taskScheduler.schedule(tarea, new Date()); // inmediato pero asíncrono
        }
    }
}

