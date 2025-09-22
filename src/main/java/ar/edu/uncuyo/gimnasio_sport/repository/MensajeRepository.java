package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.entity.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m JOIN m.usuario u " +
            "WHERE m.eliminado = false " +
            "AND m.tipo = :tipo " +
            "AND m.asunto LIKE CONCAT('%', :asunto, '%') " +
            "AND u.nombreUsuario LIKE CONCAT('%', :nombreUsuario, '%')")

    Page<Mensaje> filtrar(@Param("tipo") TipoMensaje tipo,
                          @Param("asunto") String asunto,
                          @Param("nombreUsuario") String nombreUsuario,
                          Pageable pageable);

    Optional<Mensaje> findByIdAndEliminadoFalse(Long id);
}
