package ar.edu.uncuyo.gimnasio_sport.init.geo;

import lombok.Data;

@Data
public class ProvinciaDTO {
    private String id;
    private String nombre;

    public Long getIdAsLong() {
        return Long.parseLong(id);
    }
}
