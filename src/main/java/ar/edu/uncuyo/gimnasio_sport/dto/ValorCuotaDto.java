package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValorCuotaDto {
    private Long id;

    @NotNull(message = "{NotNull.valorCuota.fechaDesde}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaDesde;

    @NotNull(message = "{NotNull.valorCuota.fechaHasta}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaHasta;

    @NotNull(message = "{NotNull.valorCuota.valorCuota}")
    @DecimalMin(value = "0.0", inclusive = false)
    private Double valorCuota;
}
