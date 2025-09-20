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
    private Long numeroFactura;
    private LocalDate fechaFactura;
    private Double totalPagado;
    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;
    private boolean eliminado;
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> detalles = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "forma_de_pago_id")
    private FormaDePago formaDePago;
}
