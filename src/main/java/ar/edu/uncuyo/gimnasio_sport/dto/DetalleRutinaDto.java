package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRutinaDto {

    private Long id;
    private Date fecha;
    private String actividad;
    private EstadoRutina estadoRutina;
    private boolean eliminado;
    private Long rutinaId;
}
