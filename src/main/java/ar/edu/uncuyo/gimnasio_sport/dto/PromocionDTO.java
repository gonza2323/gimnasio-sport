package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PromocionDTO {

    private Long id;

    @NotBlank
    @Size(max = 200)
    private String asunto;

    @NotBlank
    @Size(max = 4000)
    private String cuerpo;

    @NotNull
    private LocalDateTime fechaProgramada;

    private LocalDate fechaEnvioPromocion;
    private Long cantidadSociosEnviados;
}
