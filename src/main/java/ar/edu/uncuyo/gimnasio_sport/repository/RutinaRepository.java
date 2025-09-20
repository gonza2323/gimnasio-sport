package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    Optional<Rutina> findByIdAndEliminadoFalse(Long id);

    List<Rutina> findAllByEliminadoFalse();
}

