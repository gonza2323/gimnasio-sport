package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.entity.FormaDePago;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoFactura;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDto {
    private Long id;
    @NotNull
    @Size(min = 8, max = 12)
    private Long numeroFactura;
    @NotNull
    @PastOrPresent
    private LocalDate fechaFactura;
    @NotNull
    @PositiveOrZero
    private Double totalPagado;
    @NotNull
    private EstadoFactura estado;
    @NotNull
    private boolean eliminado;

    private List<DetalleFacturaDto> detalles;
    private FormaDePago formaDePago;
}
