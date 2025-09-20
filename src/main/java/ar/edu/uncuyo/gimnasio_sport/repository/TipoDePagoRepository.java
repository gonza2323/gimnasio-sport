package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.enums.TipoDePago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDePagoRepository extends JpaRepository<TipoDePago,Long> {
    boolean existsByTipoDePago(TipoDePago tipoDePago);
}
