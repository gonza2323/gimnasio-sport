package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empleado extends Persona {
    @Column(nullable = false)
    private TipoEmpleado tipoEmpleado;
}
