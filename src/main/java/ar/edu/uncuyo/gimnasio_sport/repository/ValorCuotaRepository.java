package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ValorCuotaRepository extends JpaRepository<ValorCuota, Long> {

    List<ValorCuota> findAllByEliminadoFalseOrderByFechaDesdeDesc();

    @Query("""
           SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
           FROM ValorCuota v
           WHERE v.eliminado = false
             AND v.fechaDesde <= :fechaHasta
             AND v.fechaHasta >= :fechaDesde
           """)
    boolean existeValorCuotaRangoVigenciaSuperpuesto(LocalDate fechaDesde, LocalDate fechaHasta);

    @Query("""
           SELECT v 
           FROM ValorCuota v 
           WHERE v.eliminado = false
             AND v.fechaDesde <= :fechaActual 
             AND v.fechaHasta >= :fechaActual
           """)
    Optional<ValorCuota> buscarValorCuotaVigente(LocalDate fechaActual);
}
