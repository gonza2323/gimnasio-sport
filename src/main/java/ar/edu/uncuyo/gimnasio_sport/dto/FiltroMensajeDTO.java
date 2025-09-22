package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiltroMensajeDTO {
    private TipoMensaje tipoMensaje;
    private String asuntoContiene;
    private String nombreUsuario;
}
