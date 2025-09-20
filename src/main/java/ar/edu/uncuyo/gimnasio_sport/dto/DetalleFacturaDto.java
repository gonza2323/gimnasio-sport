package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaDto {
    private Long id;
    private boolean eliminado;
    private Long facturaId;
}
