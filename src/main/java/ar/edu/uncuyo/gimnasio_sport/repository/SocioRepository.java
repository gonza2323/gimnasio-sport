package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SocioRepository extends JpaRepository<Socio, Long> {
    @Query("SELECT MAX(s.numeroSocio) FROM Socio s")
    Long findMaxNumeroSocio();

    List<Socio> findAllByEliminadoFalse();

    List<Socio> findAllBySucursalAndEliminadoFalse(Sucursal sucursal);

    Optional<Socio> findByIdAndEliminadoFalse(Long id);
}
