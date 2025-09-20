package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaDto {
    private Long id;

    @NotBlank(message = "NotBlank.provincia.nombre")
    @Size(min = 2, max = 255, message = "Size.provincia.nombre")
    private String nombre;

    @NotNull(message = "NotNull.provincia.paisId")
    private Long paisId;
}
