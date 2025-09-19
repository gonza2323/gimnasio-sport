package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioCreateFormDto {
    private Long socioId;

    @Valid
    private PersonaCreateFormDTO persona;
}
