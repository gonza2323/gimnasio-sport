package ar.edu.uncuyo.gimnasio_sport.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocioCreateFormDto {
    private Long socioId;

    @Valid
    private PersonaCreateFormDTO persona;
}
