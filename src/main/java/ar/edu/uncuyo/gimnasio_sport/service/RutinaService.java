package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleRutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
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
import org.springframework.web.util.HtmlUtils;
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
        RutinaDto clean = sanitizeInput(dto);
        Rutina rutina = rutinaMapper.toEntity(clean);
        rutina.setEliminado(false);
        aplicarDependencias(rutina, clean);
        Rutina guardada = rutinaRepository.save(rutina);
        return prepararRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listar() {
        return rutinaRepository.findAllByEliminadoFalse()
                .stream()
                .map(this::prepararRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public RutinaDto buscarPorId(Long id) {
        Rutina rutina = obtenerRutinaActiva(id);
        return prepararRespuesta(rutina);
    }

    @Transactional
    public RutinaDto actualizar(Long id, RutinaDto dto) {
        validarRutina(dto);
        RutinaDto clean = sanitizeInput(dto);
        Rutina rutina = obtenerRutinaActiva(id);
        rutinaMapper.updateEntityFromDto(clean, rutina);
        aplicarDependencias(rutina, clean);
        Rutina actualizada = rutinaRepository.save(rutina);
        return prepararRespuesta(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        Rutina rutina = obtenerRutinaActiva(id);
        rutina.setEliminado(true);
        rutinaRepository.save(rutina);
    }

    @Transactional
    public DetalleRutinaDto crearDetalle(Long rutinaId, DetalleRutinaDto dto) {
        validarDetalleRutina(dto);
        Rutina rutina = obtenerRutinaActiva(rutinaId);
        DetalleRutinaDto clean = sanitizeDetalleInput(dto);
        DetalleRutina detalle = detalleRutinaMapper.toEntity(clean);
        detalle.setRutina(rutina);
        detalle.setEliminado(false);
        DetalleRutina guardado = detalleRutinaRepository.save(detalle);
        return sanitizeDetalleForResponse(detalleRutinaMapper.toDto(guardado));
    }

    @Transactional(readOnly = true)
    public DetalleRutinaDto buscarDetalle(Long idDetalle) {
        DetalleRutina detalle = obtenerDetalleActivo(idDetalle);
        return sanitizeDetalleForResponse(detalleRutinaMapper.toDto(detalle));
    }

    @Transactional
    public DetalleRutinaDto modificarDetalle(Long idDetalle, DetalleRutinaDto dto) {
        validarDetalleRutina(dto);
        DetalleRutina detalle = obtenerDetalleActivo(idDetalle);
        DetalleRutinaDto clean = sanitizeDetalleInput(dto);
        detalleRutinaMapper.updateFromDto(clean, detalle);
        DetalleRutina actualizado = detalleRutinaRepository.save(detalle);
        return sanitizeDetalleForResponse(detalleRutinaMapper.toDto(actualizado));
    }

    @Transactional
    public void eliminarDetalle(Long idDetalle) {
        DetalleRutina detalle = obtenerDetalleActivo(idDetalle);
        detalle.setEliminado(true);
        detalleRutinaRepository.save(detalle);
    }

    // --- Validaciones y dependencias ---

    private void validarRutina(RutinaDto dto) {
        if (dto == null) {
            throw new BusinessException("rutina.datos.nulos");
        }
        if (dto.getTipo() == null) {
            throw new BusinessException("rutina.tipo.requerido");
        }
        if (dto.getFechaInicio() == null) {
            throw new BusinessException("rutina.fechaInicio.requerida");
        }
        if (dto.getFechaFinalizacion() == null) {
            throw new BusinessException("rutina.fechaFin.requerida");
        }

        Date inicio = dto.getFechaInicio();
        Date fin = dto.getFechaFinalizacion();
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

        if (dto.getSocioId() == null || dto.getSocioId() <= 0) {
            throw new BusinessException("rutina.socio.id.invalido");
        }
        if (dto.getProfesorId() == null || dto.getProfesorId() <= 0) {
            throw new BusinessException("rutina.profesor.id.invalido");
        }
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

    private Rutina obtenerRutinaActiva(Long id) {
        Long validId = validarRutinaId(id, "rutina.id.invalido");
        return rutinaRepository.findByIdAndEliminadoFalse(validId)
                .orElseThrow(() -> new BusinessException("rutina.noEncontrada"));
    }

    private DetalleRutina obtenerDetalleActivo(Long idDetalle) {
        Long validId = validarDetalleId(idDetalle, "rutina.detalle.id.invalido");
        DetalleRutina detalle = detalleRutinaRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.detalle.noEncontrado"));
        if (detalle.isEliminado()) {
            throw new BusinessException("rutina.detalle.noEncontrado");
        }
        return detalle;
    }

    private Socio obtenerSocio(Long socioId) {
        Long validId = validarIdentificador(socioId, "rutina.socio.id.invalido");
        return socioRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.socio.noEncontrado"));
    }

    private Empleado obtenerProfesor(Long profesorId) {
        Long validId = validarIdentificador(profesorId, "rutina.profesor.id.invalido");
        return empleadoRepository.findById(validId)
                .orElseThrow(() -> new BusinessException("rutina.profesor.noEncontrado"));
    }

    private void aplicarDependencias(Rutina rutina, RutinaDto dto) {
        rutina.setUsuario(obtenerSocio(dto.getSocioId()));
        rutina.setProfesor(obtenerProfesor(dto.getProfesorId()));
    }

    private RutinaDto prepararRespuesta(Rutina rutina) {
        return sanitizeForResponse(rutinaMapper.toDto(rutina));
    }

    private Long validarIdentificador(Long value, String errorCode) {
        if (value == null || value <= 0) {
            throw new BusinessException(errorCode);
        }
        return value;
    }

    private Long validarDetalleId(Long value, String errorCode) {
        return validarIdentificador(value, errorCode);
    }

    private Long validarDetalleIdOpcional(Long value) {
        if (value == null) {
            return null;
        }
        return validarDetalleId(value, "rutina.detalle.id.invalido");
    }

    // --- Sanitización y copias defensivas ---

    private RutinaDto sanitizeInput(RutinaDto dto) {
        RutinaDto clean = new RutinaDto();
        clean.setId(validarRutinaIdOpcional(dto.getId(), "rutina.id.invalido"));
        clean.setTipo(dto.getTipo());
        clean.setFechaInicio(cloneDate(dto.getFechaInicio()));
        clean.setFechaFinalizacion(cloneDate(dto.getFechaFinalizacion()));
        clean.setSocioId(validarIdentificador(dto.getSocioId(), "rutina.socio.id.invalido"));
        clean.setProfesorId(validarIdentificador(dto.getProfesorId(), "rutina.profesor.id.invalido"));
        return clean;
    }

    private RutinaDto sanitizeForResponse(RutinaDto dto) {
        if (dto == null) {
            return null;
        }
        RutinaDto safe = new RutinaDto();
        safe.setId(dto.getId());
        safe.setTipo(dto.getTipo());
        safe.setFechaInicio(cloneDate(dto.getFechaInicio()));
        safe.setFechaFinalizacion(cloneDate(dto.getFechaFinalizacion()));
        safe.setSocioId(dto.getSocioId());
        safe.setProfesorId(dto.getProfesorId());
        safe.setDetalles(sanitizeDetalleList(dto.getDetalles()));
        return safe;
    }

    private DetalleRutinaDto sanitizeDetalleInput(DetalleRutinaDto dto) {
        DetalleRutinaDto clean = new DetalleRutinaDto();
        clean.setId(validarDetalleIdOpcional(dto.getId()));
        clean.setFecha(cloneDate(dto.getFecha()));
        clean.setActividad(sanitizeText(dto.getActividad()));
        clean.setEstadoRutina(dto.getEstadoRutina());
        clean.setEliminado(false);
        clean.setRutinaId(validarRutinaIdOpcional(dto.getRutinaId(), "rutina.id.invalido"));
        return clean;
    }

    private DetalleRutinaDto sanitizeDetalleForResponse(DetalleRutinaDto dto) {
        if (dto == null) {
            return null;
        }
        DetalleRutinaDto safe = new DetalleRutinaDto();
        safe.setId(dto.getId());
        safe.setFecha(cloneDate(dto.getFecha()));
        safe.setActividad(dto.getActividad());
        safe.setEstadoRutina(dto.getEstadoRutina());
        safe.setEliminado(dto.isEliminado());
        safe.setRutinaId(dto.getRutinaId());
        return safe;
    }

    private List<DetalleRutinaDto> sanitizeDetalleList(List<DetalleRutinaDto> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return List.of();
        }
        return detalles.stream()
                .map(this::sanitizeDetalleForResponse)
                .toList();
    }

    private String sanitizeText(String value) {
        return value == null ? null : HtmlUtils.htmlEscape(value, "UTF-8");
    }

    private Date cloneDate(Date source) {
        return source == null ? null : new Date(source.getTime());
    }

    private Long validarRutinaId(Long value, String errorCode) {
        return validarIdentificador(value, errorCode);
    }

    private Long validarRutinaIdOpcional(Long value, String errorCode) {
        if (value == null) {
            return null;
        }
        return validarRutinaId(value, errorCode);
    }
}
