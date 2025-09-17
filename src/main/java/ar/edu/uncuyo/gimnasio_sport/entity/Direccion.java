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
    public Long id;

    public String calle;
    public String numeracion;
    public String barrio;
    public String manzanaPiso;
    public String casaDepartamento;
    public String referencia;

    public boolean eliminado;

    @ManyToOne
    public Localidad localidad;
}
