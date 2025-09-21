package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDto {
    private Long id;

    @NotBlank(message = "{NotBlank.sucursal.nombre}")
    @Size(min = 1, max = 50, message = "{Size.sucursal.nombre}")
    private String nombre;

    @Valid
    private DireccionDto direccion = new DireccionDto();
}
