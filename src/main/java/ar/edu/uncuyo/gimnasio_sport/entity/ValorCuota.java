package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ValorCuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    LocalDate fechaDesde;

    @Column(nullable = false)
    LocalDate fechaHasta;

    @Column(nullable = false)
    Double valorCuota;

    @Column(nullable = false)
    boolean eliminado;
}
