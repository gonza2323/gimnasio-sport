package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoCuotasDto {
    private List<Long> cuotasSeleccionadas;
}
