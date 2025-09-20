package ar.edu.uncuyo.gimnasio_sport.dto;

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
public class ProvinciaListaDto {
    private Long id;

    private String nombre;

    private String paisNombre;
}
