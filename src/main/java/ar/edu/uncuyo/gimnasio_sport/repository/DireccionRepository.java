package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    Optional<Direccion> findByIdAndEliminadoFalse(Long id);
}
