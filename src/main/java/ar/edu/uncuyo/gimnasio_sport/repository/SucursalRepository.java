package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);

    Optional<Sucursal> findByIdAndEliminadoFalse(Long id);
}
