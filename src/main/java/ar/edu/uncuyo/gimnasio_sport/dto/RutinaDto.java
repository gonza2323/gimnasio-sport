package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutinaDto {
    private Long id;
    private EstadoRutina tipo;
    private Date fechaInicio;
    private Date fechaFinalizacion;
    private Long socioId;
    private Long profesorId;
}

