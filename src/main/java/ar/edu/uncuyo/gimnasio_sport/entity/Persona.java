package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false)
    private String numeroDocumento;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String correoElectronico;

    @Column(nullable = false)
    private boolean eliminado;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Direccion direccion;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Sucursal sucursal;

    @JoinColumn(nullable = false)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Usuario usuario;
}
