package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.Mes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Enumerated(EnumType.STRING)
    private Mes mes;

    private Long anio;

    @Enumerated(EnumType.STRING)
    private EstadoCuota estado;

    private LocalDate fechaVencimiento;

    private boolean eliminado;

    @OneToMany(mappedBy = "cuotaMensual", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalleCollection = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false) // FK a Socio
    private Socio socio;
}
