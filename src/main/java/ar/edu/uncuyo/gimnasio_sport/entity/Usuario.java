package ar.edu.uncuyo.gimnasio_sport.entity;

import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreUsuario;
    private String clave;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    private boolean eliminado;
}
