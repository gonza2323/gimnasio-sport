package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RutinaDto {

    private Long id;

    @NotNull(message = "{NotNull.rutina.tipo}")
    private EstadoRutina tipo;

    @NotNull(message = "{NotNull.rutina.fechaInicio}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @NotNull(message = "{NotNull.rutina.fechaFinalizacion}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFinalizacion;

    @NotNull(message = "{NotNull.rutina.socio}")
    private Long socioId;

    private Long profesorId;

    @Valid
    private List<DetalleRutinaDto> detalles = new ArrayList<DetalleRutinaDto>();

    private String socioNombre;

    private String socioApellido;

    private String socioEmail;

    private Long socioNumero;

    private String profesorNombre;

    private String profesorApellido;
}
