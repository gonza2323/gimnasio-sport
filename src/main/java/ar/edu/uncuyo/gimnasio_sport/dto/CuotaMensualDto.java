package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaMensualDto {
    private Long id;
    private Month mes;
    private Long anio;
    private EstadoCuota estado;
    private LocalDate fechaVencimiento;
    private boolean eliminado;
    private Long socioId;
}
