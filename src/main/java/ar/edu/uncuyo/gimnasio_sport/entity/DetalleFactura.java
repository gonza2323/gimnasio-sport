package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DetalleFactura {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;
}
