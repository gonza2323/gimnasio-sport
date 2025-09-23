package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaMensualCreateDto {
    private Long id;

    @NotNull
    private Month mes;

    @NotNull
    private Long anio;

    @NotNull
    private EstadoCuota estado;

    @NotNull
    private LocalDate fechaVencimiento;

    @NotNull
    private Long valorCuotaId;

    @NotNull
    private Long socioId;
}
