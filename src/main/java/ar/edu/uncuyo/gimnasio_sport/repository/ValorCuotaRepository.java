package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ValorCuotaRepository extends JpaRepository<ValorCuota, Long> {
    boolean existsByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqual(
            java.time.LocalDate fechaHasta, java.time.LocalDate fechaDesde);

    List<ValorCuota> findAllByEliminadoFalse();



}
