package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
//@Table(name = "promociones")
@Data
@EqualsAndHashCode(callSuper = true)
public class Promocion extends Mensaje {

    private LocalDate fechaEnvioPromocion;
    private long cantidadSociosEnviados;
}
