package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalidadDto {
    private Long id;

    @NotBlank(message = "NotBlank.localidad.nombre")
    @Size(min = 2, max = 255, message = "Size.localidad.nombre")
    private String nombre;

    @NotBlank(message = "NotBlank.localidad.codigoPostal")
    @Size(min = 1, max = 50, message = "Size.localidad.codigoPostal")
    private String codigoPostal;

    @NotNull(message = "NotNull.localidad.departamentoId")
    private Long departamentoId;
}
