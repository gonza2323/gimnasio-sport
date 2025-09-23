package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {
    boolean existsBySocioIdAndMesAndAnioAndEliminadoFalse(Long idSocio, Month mes, Long anio);

    List<CuotaMensual> findAllByEliminadoFalse();

    List<CuotaMensual> findAllByEliminadoFalseAndEstado(EstadoCuota estado);

    List<CuotaMensual> findAllByFechaVencimientoBetween(LocalDate fechaDesde, LocalDate fechaHasta);

    List<CuotaMensual> findAllBySocioIdAndEliminadoFalse(Long socioId);

    @Query("""
            SELECT COALESCE(SUM(c.valorCuota.valorCuota), 0)
            FROM CuotaMensual c
            WHERE c.eliminado = false
            AND c.estado = ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota.ADEUDADA
            AND c.socio.id = :socioId
            """)
    double getDeudaTotalDeSocio(Long socioId);
}
