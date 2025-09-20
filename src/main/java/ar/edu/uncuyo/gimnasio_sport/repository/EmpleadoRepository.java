package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
