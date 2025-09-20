package ar.edu.uncuyo.gimnasio_sport.dto;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCreateFormDTO {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    private String nombre;

    @NotBlank
    @Size(min = 1, max = 255)
    private String apellido;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    private TipoDocumento tipoDocumento;

    @NotBlank
    @Size(min = 7, max = 50)
    private String numeroDocumento;

    @NotBlank
    @Size(min = 6, max = 50)
    private String telefono;

    @Email
    private String correoElectronico;

    @Valid
    private DireccionDto direccion;

    @Valid
    private UsuarioCreateFormDTO usuario;

    @NotNull
    private Long sucursalId;
}
