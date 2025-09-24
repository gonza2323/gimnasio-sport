package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleRutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.DetalleRutinaMapper;
import ar.edu.uncuyo.gimnasio_sport.mapper.RutinaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.DetalleRutinaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpleadoRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.RutinaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private static final long DURACION_MINIMA_MS = TimeUnit.DAYS.toMillis(1);
    private static final long DURACION_MAXIMA_MS = TimeUnit.DAYS.toMillis(365);

    private final RutinaRepository rutinaRepository;
    private final SocioRepository socioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RutinaMapper rutinaMapper;
    private final DetalleRutinaRepository detalleRutinaRepository;
    private final DetalleRutinaMapper detalleRutinaMapper;


    @Transactional
    public RutinaDto crear(RutinaDto dto) {
        validarRutina(dto);
        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setEliminado(false);
        relacionarSocioYProfesor(rutina, dto.getSocioId(), dto.getProfesorId());

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            String raw = dto.getDetalles().get(0).getActividad(); // texto plano: "Pecho; Espalda; Piernas"
            if (raw != null && !raw.isBlank()) {
                // limpiamos lo que pudo mapear el mapper (evita guardar el item "crudo")
                rutina.getDetalles().clear();

                String[] partes = raw.split(";");
                for (String parte : partes) {
                    String texto = parte.trim();
                    if (!texto.isEmpty()) {
                        DetalleRutina detalle = new DetalleRutina();
                        detalle.setRutina(rutina);
                        detalle.setActividad(texto);
                        detalle.setFecha(new Date());
                        detalle.setEstadoRutina(dto.getTipo()); // o un valor por defecto (p.ej. EstadoRutina.SIN_FINALIZAR)
                        detalle.setEliminado(false);
                        rutina.getDetalles().add(detalle);
                    }
                }
            }
        }

        Rutina guardada = rutinaRepository.save(rutina);
        return toDto(guardada);
    }


    @Transactional(readOnly = true)
    public List<RutinaDto> listar() {
        List<Rutina> rutinas = rutinaRepository.findAllByEliminadoFalse();
        return rutinas.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorProfesor(Long profesorId) {
        if (profesorId == null) {
            return List.of();
        }
        List<Rutina> rutinas = rutinaRepository.findAllByProfesor_IdAndEliminadoFalse(profesorId);
        return rutinas.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorSocio(Long socioId) {
        if (socioId == null) {
            return List.of();
        }
        List<Rutina> rutinas = rutinaRepository.findAllByUsuario_IdAndEliminadoFalse(socioId);
        return rutinas.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RutinaDto buscarPorId(Long id) {
        Rutina rutina = obtenerRutina(id);
        return toDto(rutina);
    }

    @Transactional
    public RutinaDto actualizar(Long id, RutinaDto dto) {
        validarRutina(dto);
        Rutina rutina = obtenerRutina(id);
        rutinaMapper.updateEntityFromDto(dto, rutina);
        relacionarSocioYProfesor(rutina, dto.getSocioId(), dto.getProfesorId());


        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            String raw = dto.getDetalles().get(0).getActividad(); // texto plano: "Pecho; Espalda; Piernas"
            if (raw != null && !raw.isBlank()) {
                // limpiamos los detalles existentes
                rutina.getDetalles().clear();

                String[] partes = raw.split(";");
                for (String parte : partes) {
                    String texto = parte.trim();
                    if (!texto.isEmpty()) {
                        DetalleRutina detalle = new DetalleRutina();
                        detalle.setRutina(rutina);
                        detalle.setActividad(texto);
                        detalle.setFecha(new Date());
                        detalle.setEstadoRutina(dto.getTipo()); // o un valor por defecto (p.ej. EstadoRutina.SIN_FINALIZAR)
                        detalle.setEliminado(false);
                        rutina.getDetalles().add(detalle);
                    }
                }
            }
        }

        Rutina actualizada = rutinaRepository.save(rutina);
        return toDto(actualizada);
    }


    @Transactional
    public void eliminar(Long id) {
        Rutina rutina = obtenerRutina(id);
        rutina.setEliminado(true);
        rutinaRepository.save(rutina);
    }

    @Transactional
    public DetalleRutinaDto crearDetalle(Long rutinaId, DetalleRutinaDto dto) {
        validarDetalleRutina(dto);
        Rutina rutina = obtenerRutina(rutinaId);
        DetalleRutina detalle = detalleRutinaMapper.toEntity(dto);
        detalle.setRutina(rutina);
        detalle.setEliminado(false);
        DetalleRutina guardado = detalleRutinaRepository.save(detalle);
        return toDetalleDto(guardado);
    }

    @Transactional(readOnly = true)
    public DetalleRutinaDto buscarDetalle(Long idDetalle) {
        DetalleRutina detalle = obtenerDetalle(idDetalle);
        return toDetalleDto(detalle);
    }

    @Transactional
    public DetalleRutinaDto actualizarDetalle(Long idDetalle, DetalleRutinaDto dto) {
        validarDetalleRutina(dto);
        DetalleRutina detalle = obtenerDetalle(idDetalle);
        detalleRutinaMapper.updateFromDto(dto, detalle);
        DetalleRutina actualizado = detalleRutinaRepository.save(detalle);
        return toDetalleDto(actualizado);
    }

    @Transactional
    public void eliminarDetalle(Long idDetalle) {
        DetalleRutina detalle = obtenerDetalle(idDetalle);
        detalle.setEliminado(true);
        detalleRutinaRepository.save(detalle);
    }

    private Rutina obtenerRutina(Long id) {
        Long validId = validarId(id, "rutina.id.invalido");
        return rutinaRepository.findByIdAndEliminadoFalse(validId)
                .orElseThrow(() -> new BusinessException("rutina.noEncontrada"));
    }

    private DetalleRutina obtenerDetalle(Long idDetalle) {
        Long validId = validarId(idDetalle, "rutina.detalle.id.invalido");
        DetalleRutina detalle = detalleRutinaRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.detalle.noEncontrado"));
        if (detalle.isEliminado()) {
            throw new BusinessException("rutina.detalle.noEncontrado");
        }
        return detalle;
    }

    private void relacionarSocioYProfesor(Rutina rutina, Long socioId, Long profesorId) {
        rutina.setUsuario(obtenerSocio(socioId));
        rutina.setProfesor(obtenerProfesor(profesorId));
    }

    private Socio obtenerSocio(Long socioId) {
        Long validId = validarId(socioId, "rutina.socio.id.invalido");
        return socioRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.socio.noEncontrado"));
    }

    private Empleado obtenerProfesor(Long profesorId) {
        Long validId = validarId(profesorId, "rutina.profesor.id.invalido");
        return empleadoRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.profesor.noEncontrado"));
    }

    private void validarRutina(RutinaDto dto) {
        if (dto == null) {
            throw new BusinessException("rutina.datos.nulos");
        }
        if (dto.getTipo() == null) {
            throw new BusinessException("rutina.tipo.requerido");
        }

        Date inicio = dto.getFechaInicio();
        if (inicio == null) {
            throw new BusinessException("rutina.fechaInicio.requerida");
        }

        Date fin = dto.getFechaFinalizacion();
        if (fin == null) {
            throw new BusinessException("rutina.fechaFin.requerida");
        }
        if (!fin.after(inicio)) {
            throw new BusinessException("rutina.fechas.orden");
        }

        long duracion = fin.getTime() - inicio.getTime();
        if (duracion < DURACION_MINIMA_MS) {
            throw new BusinessException("rutina.duracion.minima");
        }
        if (duracion > DURACION_MAXIMA_MS) {
            throw new BusinessException("rutina.duracion.maxima");
        }

        validarId(dto.getSocioId(), "rutina.socio.id.invalido");
        validarId(dto.getProfesorId(), "rutina.profesor.id.invalido");
    }

    private void validarDetalleRutina(DetalleRutinaDto dto) {
        if (dto == null) {
            throw new BusinessException("rutina.detalle.datos.nulos");
        }
        if (dto.getFecha() == null) {
            throw new BusinessException("rutina.detalle.fecha.requerida");
        }
        if (!StringUtils.hasText(dto.getActividad())) {
            throw new BusinessException("rutina.detalle.actividad.requerida");
        }
        if (dto.getEstadoRutina() == null) {
            throw new BusinessException("rutina.detalle.estado.requerido");
        }
    }

    private RutinaDto toDto(Rutina rutina) {
        RutinaDto dto = rutinaMapper.toDto(rutina);

        if (rutina.getUsuario() != null) {
            dto.setSocioNombre(nombreCompleto(rutina.getUsuario()));
            dto.setSocioEmail(rutina.getUsuario().getCorreoElectronico());
            dto.setSocioNumero(rutina.getUsuario().getNumeroSocio());
        }
        if (rutina.getProfesor() != null) {
            dto.setProfesorNombre(nombreCompleto(rutina.getProfesor()));
        }
        if (dto.getDetalles() == null) {
            dto.setDetalles(List.of());
        }

        // 🔹 ajustar campo actividad para que tenga "actividad - estado - fecha"
        for (DetalleRutinaDto d : dto.getDetalles()) {
            String actividad = d.getActividad() != null ? d.getActividad() : "";
            String estado    = d.getEstadoRutina() != null ? d.getEstadoRutina().name() : "";
            String fecha     = d.getFecha() != null ? d.getFecha().toString().substring(0, 10) : "";
            d.setActividad(actividad + " - " + estado + " - " + fecha);
        }

        return dto;
    }



    private DetalleRutinaDto toDetalleDto(DetalleRutina detalle) {
        DetalleRutinaDto dto = detalleRutinaMapper.toDto(detalle);
        if (dto.getRutinaId() == null && detalle.getRutina() != null) {
            dto.setRutinaId(detalle.getRutina().getId());
        }
        return dto;
    }

    private Long validarId(Long value, String errorCode) {
        if (value == null || value <= 0) {
            throw new BusinessException(errorCode);
        }
        return value;
    }

    private String nombreCompleto(Persona persona) {
        if (persona == null) {
            return null;
        }
        String nombre = StringUtils.hasText(persona.getNombre()) ? persona.getNombre().trim() : "";
        String apellido = StringUtils.hasText(persona.getApellido()) ? persona.getApellido().trim() : "";
        return (nombre + " " + apellido).trim();
    }
}
