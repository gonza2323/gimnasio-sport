package ar.edu.uncuyo.gimnasio_sport.entities;

import ar.edu.uncuyo.gimnasio_sport.model.Mensaje;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Date;


@Entity
@Table(name ="promociones")
@Data

public class Promocion extends Mensaje {

    private Date fechaEnvioPromocion;
    private long cantidadSociosEnviados;

}
