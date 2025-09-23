package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
@Builder
public class CuotaMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Month mes;

    @Column(nullable = false)
    private Long anio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoCuota estado;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @Column(nullable = false)
    private boolean eliminado;

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false) // FK a Socio
    private Socio socio;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ValorCuota valorCuota;

    @OneToMany(mappedBy = "cuotaMensual", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalleCollection = new ArrayList<>();
}
