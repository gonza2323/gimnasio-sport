package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocioRepository extends JpaRepository<Socio, Long> {
    @Query("SELECT MAX(s.numeroSocio) FROM Socio s")
    Long findMaxNumeroSocio();
}
