package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResumenDTO {
    private Long id;
    private String nombre;
    private String nombrePais;
    private String nombreProvincia;
}
