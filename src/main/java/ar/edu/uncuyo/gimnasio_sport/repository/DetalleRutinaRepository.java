package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.DetalleRutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleRutinaRepository extends JpaRepository<DetalleRutina, Long> {
    List<DetalleRutina> findAllByRutinaAndEliminadoFalse(Rutina rutina);
}
