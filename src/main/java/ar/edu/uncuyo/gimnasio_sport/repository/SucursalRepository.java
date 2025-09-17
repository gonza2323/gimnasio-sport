package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    boolean existsByIdAndEliminadoFalse(Long id);
    boolean existsByNombreAndEliminadoFalse(String nombre);
    List<Sucursal> findAllByEliminadoFalseOrderByNombre();
    List<Sucursal> findAllByNombre(String nombre);

    Optional<Sucursal> findByIdAndEliminadoFalse(Long id);
    Optional<Sucursal> findByNombreAndEliminadoFalse(String nombre);
    Optional<Sucursal> findByNombreAndEliminadoFalse(String nombre, Empresa empresa);
}
