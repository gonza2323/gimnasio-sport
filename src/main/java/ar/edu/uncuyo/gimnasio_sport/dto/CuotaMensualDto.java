package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.entity.DetalleFactura;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.Mes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaMensualDto {
    private Long id;
    private Mes mes;
    private Long anio;
    private EstadoCuota estado;
    private LocalDate fechaVencimiento;
    private boolean eliminado;
    private Long idSocio;
    private List<DetalleFactura> detalleCollection = new ArrayList<>();
}
