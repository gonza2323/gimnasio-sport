package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);
    Optional<Departamento> findByIdAndEliminadoFalse(Long id);
}
