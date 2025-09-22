package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
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
public class UsuarioCreateFormDTO {
    private Long id;

    private String nombreUsuario;

    @NotBlank(message = "{NotBlank.usuario.clave}")
    @Size(min = 8, max = 255, message = "{Size.usuario.clave}")
    private String clave;

    @NotBlank(message = "{NotBlank.usuario.confirmacionClave}")
    @Size(min = 8, max = 255, message = "{Size.usuario.confirmacionClave}")
    private String confirmacionClave;

    private RolUsuario rol;
}
