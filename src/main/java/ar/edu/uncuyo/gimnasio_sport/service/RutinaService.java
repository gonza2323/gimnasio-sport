package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.*;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private static final long DURACION_MINIMA_MS = 1;
    private static final long DURACION_MAXIMA_MS = 365;

    private final RutinaRepository rutinaRepository;
    private final SocioRepository socioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RutinaMapper rutinaMapper;
    private final DetalleRutinaRepository detalleRutinaRepository;
    private final DetalleRutinaMapper detalleRutinaMapper;


    @Transactional
    public Rutina crear(RutinaDto dto) {

        validarRutina(dto);
        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setEliminado(false);
        relacionarSocioYProfesor(rutina, dto.getSocioId(), dto.getProfesorId());
        rutinaRepository.save(rutina);

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            List<DetalleRutina> detalles = dto.getDetalles().stream().map(detalleDto -> {
                DetalleRutina detalle = detalleRutinaMapper.toEntity(detalleDto);
                detalle.setRutina(rutina);
                return detalle;
            }).toList();

            detalleRutinaRepository.saveAll(detalles);
        }

        return rutina;
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
        rutina.getDetalles().clear();

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (DetalleRutinaDto detalleDto : dto.getDetalles()) {
                detalleDto.setEliminado(false);
                DetalleRutina detalle = detalleRutinaMapper.toEntity(detalleDto);
                detalle.setRutina(rutina); // relación bidireccional
                rutina.getDetalles().add(detalle);
            }
        }

        Rutina actualizada = rutinaRepository.save(rutina); // cascade guarda detalles
        return toDto(actualizada);
    }



    @Transactional
    public void eliminar(Long id) {
        Rutina rutina = obtenerRutina(id);
        rutina.setEliminado(true);
        rutina.getDetalles().forEach(d -> d.setEliminado(true));
        rutinaRepository.save(rutina);
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

        LocalDate inicio = dto.getFechaInicio();
        if (inicio == null) {
            throw new BusinessException("rutina.fechaInicio.requerida");
        }

        LocalDate fin = dto.getFechaFinalizacion();
        if (fin == null) {
            throw new BusinessException("rutina.fechaFin.requerida");
        }
        if (!fin.isAfter(inicio)) {
            throw new BusinessException("rutina.fechas.orden");
        }

        long duracion = ChronoUnit.DAYS.between(inicio, fin);
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
