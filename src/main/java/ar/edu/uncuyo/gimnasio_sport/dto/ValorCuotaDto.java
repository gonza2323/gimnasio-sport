package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorCuotaDto {
    private Long id;
    @PastOrPresent
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double valorCuota;
    private boolean eliminado;
}
