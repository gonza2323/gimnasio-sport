package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {
    boolean existsBySocioIdAndMesAndAnioAndEliminadoFalse(Long idSocio, Month mes, Long anio);
    List<CuotaMensual> findAllByEliminadoFalse();
    List<CuotaMensual> findAllByEliminadoFalseAndEstado(EstadoCuota estado);
    List<CuotaMensual> findAllByFechaVencimientoBetween(LocalDate fechaDesde, LocalDate fechaHasta);
}
