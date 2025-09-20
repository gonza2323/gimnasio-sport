package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDto {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String calle;

    @NotBlank
    @Size(max = 20)
    private String numeracion;

    @Size(max = 20)
    private String barrio;

    @Size(max = 10)
    private String manzanaPiso;

    @Size(max = 10)
    private String casaDepartamento;

    @Size(max = 50)
    private String referencia;

    private Long paisId;

    private Long provinciaId;

    private Long departamentoId;

    @NotNull
    private Long localidadId;
}
