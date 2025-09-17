package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaisRepository extends JpaRepository<Pais, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);
    List<Pais> findAllByEliminadoFalseOrderByNombre();
    Optional<Pais> findByIdAndEliminadoFalse(Long id);
}
