package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "rutinas")
@Entity
@Data
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoRutina tipo;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinalizacion;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Socio usuario;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Empleado profesor;

    @Column
    private boolean eliminado;

    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<DetalleRutina> detalles = new ArrayList<>();
}
