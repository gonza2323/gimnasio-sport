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
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaCreateFormDTO {
    private Long id;

    @NotBlank(message = "{NotBlank.persona.nombre}")
    @Size(min = 1, max = 255, message = "{Size.persona.nombre}")
    private String nombre;

    @NotBlank(message = "{NotBlank.persona.apellido}")
    @Size(min = 1, max = 255, message = "{Size.persona.apellido}")
    private String apellido;

    @NotNull(message = "{NotNull.persona.fechaNacimiento}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotNull(message = "{NotNull.persona.tipoDocumento}")
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "{NotBlank.persona.numeroDocumento}")
    @Size(min = 6, max = 20, message = "{Size.persona.numeroDocumento}")
    private String numeroDocumento;

    @NotBlank(message = "{NotBlank.persona.telefono}")
    @Size(min = 6, max = 20, message = "{Size.persona.telefono}")
    private String telefono;

    @Email(message = "{Email.persona.correoElectronico}")
    @NotBlank(message = "{NotBlank.persona.correoElectronico}")
    private String correoElectronico;

    @Valid
    private DireccionDto direccion = new DireccionDto();

    @Valid
    private UsuarioCreateFormDTO usuario = new UsuarioCreateFormDTO();

    @NotNull(message = "{NotBlank.persona.sucursal}")
    private Long sucursalId;
}
