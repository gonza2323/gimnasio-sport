package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
