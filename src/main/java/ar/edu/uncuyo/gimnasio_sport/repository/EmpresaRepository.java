package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByNombreAndEliminadoFalse(String nombre);
    boolean existsByNombreAndIdNotAndEliminadoFalse(String nombre, Long id);

    List<Empresa> findAllByEliminadoFalseOrderByNombre();
    List<Empresa> findAllByNombre(String nombre);

    Optional<Empresa> findByIdAndEliminadoFalse(Long id);
    Optional<Empresa> findByNombreAndEliminadoFalse(String nombre);
}
