package ar.edu.uncuyo.gimnasio_sport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private String numeracion;

    private String barrio;

    private String manzanaPiso;

    private String casaDepartamento;

    private String referencia;

    @Column(nullable = false)
    private boolean eliminado;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Localidad localidad;
}
