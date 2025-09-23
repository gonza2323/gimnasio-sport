package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoFactura;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Factura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long numeroFactura;

    @Column(nullable = false)
    private LocalDate fechaFactura;

    @Column(nullable = false)
    private Double totalPagado;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    @Column(nullable = false)
    private boolean eliminado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> detalles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "forma_de_pago_id")
    private FormaDePago formaDePago;
}
