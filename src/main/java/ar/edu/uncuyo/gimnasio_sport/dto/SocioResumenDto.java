package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocioResumenDto {
    private Long id;
    private Long numeroSocio;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String nombreSucursal;
}
