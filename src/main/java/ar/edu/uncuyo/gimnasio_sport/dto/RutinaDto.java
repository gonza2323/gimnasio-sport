package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutinaDto {

    private Long id;

    private EstadoRutina tipo;

    private LocalDate fechaInicio;

    private LocalDate fechaFinalizacion;

    private Long socioId;

    private Long profesorId;

    private List<DetalleRutinaDto> detalles = new ArrayList<DetalleRutinaDto>();

    private String socioNombre;

    private String socioEmail;

    private Long socioNumero;

    private String profesorNombre;
}
