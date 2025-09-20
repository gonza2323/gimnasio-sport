package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaisDto {
    private Long id;

    @NotBlank(message = "NotBlank.pais.nombre")
    @Size(min = 2, max = 255, message = "Size.pais.nombre")
    private String nombre;
}
