package ar.edu.uncuyo.gimnasio_sport.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressDto {
    private final Long id;
    private final String calle;
    private final String numeracion;
    private final String barrio;
    private final String manzanaPiso;
    private final String casaDepartamento;
    private final String referencia;
    private final String localidadId;
}
