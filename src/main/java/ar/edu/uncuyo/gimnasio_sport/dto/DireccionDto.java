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

    @NotBlank(message = "{NotBlank.direccion.calle}")
    @Size(max = 50, message = "{Size.direccion.calle}")
    private String calle;

    @NotBlank(message = "{NotBlank.direccion.numeracion}")
    @Size(max = 20, message = "{Size.direccion.numeracion}")
    private String numeracion;

    @Size(max = 50, message = "{Size.direccion.barrio}")
    private String barrio;

    @Size(max = 10, message = "{Size.direccion.manzanaPiso}")
    private String manzanaPiso;

    @Size(max = 10, message = "{Size.direccion.casaDepartamento}")
    private String casaDepartamento;

    @Size(max = 50, message = "{Size.direccion.referencia}")
    private String referencia;

    @NotNull(message = "{NotNull.direccion.pais}")
    private Long paisId;

    @NotNull(message = "{NotNull.direccion.provincia}")
    private Long provinciaId;

    @NotNull(message = "{NotNull.direccion.departamento}")
    private Long departamentoId;

    @NotNull(message = "{NotNull.direccion.localidad}")
    private Long localidadId;
}
