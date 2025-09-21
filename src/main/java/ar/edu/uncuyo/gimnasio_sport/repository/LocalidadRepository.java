package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Localidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocalidadRepository extends JpaRepository<Localidad, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);
    Optional<Localidad> findByIdAndEliminadoFalse(Long id);

    List<Localidad> findAllByDepartamentoIdAndEliminadoFalse(Long departamentoId);
}
