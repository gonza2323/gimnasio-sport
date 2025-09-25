package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoDetalleRutina;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleRutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String actividad;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoDetalleRutina estadoRutina;

    @Column(nullable = false)
    private boolean eliminado;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Rutina rutina;
}
