package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Factura;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoFactura;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura,Long> {

    Optional<Factura> findByIdAndEliminadoFalse(Long id);
    List<Factura> findAllByEliminadoFalseOrderByFechaFacturaDescNumeroFacturaDesc();
    List<Factura> findAllByEliminadoFalseAndEstado(EstadoFactura estado);

    boolean existsByNumeroFactura(@NotNull @Size(min = 8, max = 12) Long numeroFactura);

    boolean existsByNumeroFacturaAndIdNot(@NotNull @Size(min = 8, max = 12) Long numeroFactura, Long id);

    @Query("SELECT MAX(f.numeroFactura) FROM Factura f")
    Long findMaxNumeroFactura();

    @Query("SELECT df.factura FROM DetalleFactura df WHERE df.cuotaMensual.id = :cuotaId")
    Optional<Factura> findFacturaByCuotaId(@Param("cuotaId") Long cuotaId);
}
