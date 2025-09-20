package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String titulo;

    @NotBlank
    @Size(max = 4000)
    private String texto;

    @NotNull
    private TipoMensaje tipoMensaje;

    @NotNull
    private Long usuarioId;
}
