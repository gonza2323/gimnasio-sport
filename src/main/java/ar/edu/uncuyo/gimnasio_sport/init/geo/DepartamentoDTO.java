package ar.edu.uncuyo.gimnasio_sport.init.geo;

import lombok.Data;

@Data
public class DepartamentoDTO {
    private String id;
    private String nombre;
    private ProvinciaDTO provincia;

    public Long getIdAsLong() {
        return Long.parseLong(id);
    }
}