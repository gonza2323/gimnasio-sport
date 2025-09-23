package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValorCuotaRepository extends JpaRepository<ValorCuota, Long> {
    boolean existsByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqualAndEliminadoFalse(
            java.time.LocalDate fechaHasta, java.time.LocalDate fechaDesde);

    List<ValorCuota> findAllByEliminadoFalseOrderByFechaDesdeDesc();
}
