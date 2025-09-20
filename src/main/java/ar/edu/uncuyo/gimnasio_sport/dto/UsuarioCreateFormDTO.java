package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
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
public class UsuarioCreateFormDTO {
    private Long id;

    private String nombreUsuario;

    @NotBlank(message = "")
    @Size(min = 8, max = 255, message = "")
    private String clave;

    @NotBlank(message = "")
    @Size(min = 8, max = 255, message = "")
    private String confirmacionClave;

    @NotNull
    private RolUsuario rol;
}
