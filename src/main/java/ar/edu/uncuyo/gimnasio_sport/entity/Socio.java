package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Socio extends Persona {
    @Column(nullable = false, unique = true)
    private Long numeroSocio;
}
