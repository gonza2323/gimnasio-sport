package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);
    Optional<Provincia> findByIdAndEliminadoFalse(Long id);
    List<Provincia> findAllByEliminadoFalseOrderByNombre();
}
