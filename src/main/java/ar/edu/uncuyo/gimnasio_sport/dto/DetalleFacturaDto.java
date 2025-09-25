package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaDto {
    private Long id;

    private Long facturaId;

    private Long cuotaMensualId;

    private CuotaMensualDto cuota;
}
