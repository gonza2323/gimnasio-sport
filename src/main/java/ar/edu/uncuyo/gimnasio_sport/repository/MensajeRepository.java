package ar.edu.uncuyo.gimnasio_sport.repository;

import ar.edu.uncuyo.gimnasio_sport.model.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensajeRepository extends JpaRepository<Mensaje, String> {

    @Query("select m from Mensaje m join m.usuario u " +
            "where m.eliminado = false and (:tipo is null or m.tipoMensaje = :tipo) " +
            "and (:titulo is null or lower(m.titulo) like lower(concat('%', :titulo, '%'))) " +
            "and (:nombreUsuario is null or lower(u.nombreUsuario) like lower(concat('%', :nombreUsuario, '%')))")
    Page<Mensaje> filtrar(@Param("tipo") TipoMensaje tipo,
                          @Param("titulo") String titulo,
                          @Param("nombreUsuario") String nombreUsuario,
                          Pageable pageable);
}

