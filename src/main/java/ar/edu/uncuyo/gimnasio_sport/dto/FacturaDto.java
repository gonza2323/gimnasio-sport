package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDto {
    private Long id;

    private Long numeroFactura;

    private LocalDate fechaFactura;

    private Double totalPagado;

    private EstadoFactura estado;

    private List<DetalleFacturaDto> detalles;

    private FormaDePagoDto formaDePago;
}
