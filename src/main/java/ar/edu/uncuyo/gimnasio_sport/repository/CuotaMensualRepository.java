package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.Mes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {
    boolean existsBySocioIdAndMesAndAnio(Long idSocio, Mes mes, Long anio);
    List<CuotaMensual> findAllByEliminadoFalse();
    List<CuotaMensual> findAllByEliminadoFalseAndEstado(EstadoCuota estado);
    List<CuotaMensual> findAllByFechaVencimientoBetween(LocalDate fechaDesde, LocalDate fechaHasta);

    Optional<CuotaMensual> findFirstBySocioAndEstadoAndFechaVencimientoGreaterThanEqualAndEliminadoFalse(
            Socio socio, EstadoCuota estado, LocalDate fecha
    );
}
