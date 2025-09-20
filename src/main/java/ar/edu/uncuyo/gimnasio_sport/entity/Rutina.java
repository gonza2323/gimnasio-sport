package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Table(name = "rutinas")
@Entity
@Data
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private EstadoRutina tipo;

    @Column(nullable = false)
    private Date fechaInicio;

    @Column(nullable = false)
    private Date fechaFinalizacion;

    @JoinColumn(name = "usuario_id", nullable = false)
    @ManyToOne
    private Socio usuario;

    @JoinColumn(name = "profesor_id")
    @ManyToOne
    private Empleado profesor;

    @Column
    private boolean eliminado;

}
