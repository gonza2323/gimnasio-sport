package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleRutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoDetalleRutina;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.DetalleRutinaMapper;
import ar.edu.uncuyo.gimnasio_sport.mapper.RutinaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.DetalleRutinaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private static final long DURACION_MINIMA_MS = 1;
    private static final long DURACION_MAXIMA_MS = 365;

    private final RutinaRepository rutinaRepository;
    private final RutinaMapper rutinaMapper;
    private final DetalleRutinaRepository detalleRutinaRepository;
    private final DetalleRutinaMapper detalleRutinaMapper;
    private final EmpleadoService empleadoService;
    private final SocioService socioService;


    @Transactional
    public Rutina crear(RutinaDto dto) {
        validarRutina(dto);

        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setEliminado(false);

        Socio socio = socioService.buscarSocio(dto.getSocioId());
        rutina.setUsuario(socio);

        Empleado empleado = empleadoService.buscarProfesorActual();
        rutina.setProfesor(empleado);

        rutinaRepository.save(rutina);

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            List<DetalleRutina> detalles = dto.getDetalles().stream().map(detalleDto -> {
                DetalleRutina detalle = detalleRutinaMapper.toEntity(detalleDto);
                detalle.setRutina(rutina);
                detalle.setEstadoRutina(EstadoDetalleRutina.SIN_REALIZAR);
                return detalle;
            }).toList();

            detalleRutinaRepository.saveAll(detalles);
        }

        return rutina;
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listar() {
        List<Rutina> rutinas = rutinaRepository.findAllByEliminadoFalse();
        return rutinaMapper.toDtos(rutinas);
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorProfesorActual() {
        Empleado empleadoActual = empleadoService.buscarEmpleadoActual();
        return listarPorProfesor(empleadoActual.getId());
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorProfesor(Long profesorId) {
        List<Rutina> rutinas = rutinaRepository.findAllByProfesorIdAndEliminadoFalse(profesorId);
        return rutinaMapper.toDtos(rutinas);
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorSocioActual() {
        Socio socio = socioService.buscarSocioActual();
        return listarPorSocio(socio.getId());
    }

    @Transactional(readOnly = true)
    public List<RutinaDto> listarPorSocio(Long socioId) {
        List<Rutina> rutinas = rutinaRepository.findAllByUsuarioIdAndEliminadoFalse(socioId);
        return rutinaMapper.toDtos(rutinas);
    }

    @Transactional(readOnly = true)
    public RutinaDto buscarRutina(Long id) {
        Rutina rutina = obtenerRutina(id);
        return rutinaMapper.toDto(rutina);
    }

    @Transactional
    public void actualizar(RutinaDto dto) {
        validarRutina(dto);

        Rutina rutina = obtenerRutina(dto.getId());
        rutinaMapper.updateEntityFromDto(dto, rutina);

        Socio socio = socioService.buscarSocio(dto.getSocioId());
        rutina.setUsuario(socio);

        rutinaRepository.save(rutina);

        List<DetalleRutina> detalles = detalleRutinaRepository.findAllByRutinaAndEliminadoFalse(rutina);

        Map<Long, DetalleRutina> detallesExistentesMap = detalles.stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(DetalleRutina::getId, Function.identity()));

        for (DetalleRutinaDto dtoDetalle : dto.getDetalles()) {
            if (dtoDetalle.getId() != null) {
                DetalleRutina existente = detallesExistentesMap.remove(dtoDetalle.getId());
                if (existente != null) {
                    detalleRutinaMapper.updateFromDto(dtoDetalle, existente);
                }
            } else {
                DetalleRutina nuevo = detalleRutinaMapper.toEntity(dtoDetalle);
                nuevo.setRutina(rutina);
                nuevo.setEstadoRutina(EstadoDetalleRutina.SIN_REALIZAR);
                detalles.add(nuevo);
            }
        }

        for (DetalleRutina toDelete : detallesExistentesMap.values()) {
            toDelete.setEliminado(true);
        }

        detalleRutinaRepository.saveAll(detalles);
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
        DetalleRutina detalle = obtenerDetalle(idDetalle);
        detalleRutinaMapper.updateFromDto(dto, detalle);
        DetalleRutina actualizado = detalleRutinaRepository.save(detalle);
        return toDetalleDto(actualizado);
    }

    private Rutina obtenerRutina(Long id) {
        return rutinaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("rutina.noEncontrada"));
    }

    private DetalleRutina obtenerDetalle(Long idDetalle) {
        DetalleRutina detalle = detalleRutinaRepository.findById(idDetalle)
                .orElseThrow(() -> new BusinessException("rutina.detalle.noEncontrado"));
        if (detalle.isEliminado()) {
            throw new BusinessException("rutina.detalle.noEncontrado");
        }
        return detalle;
    }

    private void validarRutina(RutinaDto dto) {
        if (dto == null) {
            throw new BusinessException("rutina.datos.nulos");
        }

        LocalDate inicio = dto.getFechaInicio();
        LocalDate fin = dto.getFechaFinalizacion();
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

        for (DetalleRutinaDto detalle : dto.getDetalles()) {
            LocalDate fecha = detalle.getFecha();
            if (fecha.isAfter(fin) || fecha.isBefore(inicio))
                throw new BusinessException("FueraDeRango.rutina.detalle.fecha");
        }
    }

    private DetalleRutinaDto toDetalleDto(DetalleRutina detalle) {
        DetalleRutinaDto dto = detalleRutinaMapper.toDto(detalle);
        if (dto.getRutinaId() == null && detalle.getRutina() != null) {
            dto.setRutinaId(detalle.getRutina().getId());
        }
        return dto;
    }

    public RutinaDto buscarRutinaDto(Long id) {
        Rutina rutina = rutinaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("rutina.noEncontrada"));
        RutinaDto rutinaDto = rutinaMapper.toDto(rutina);

        List<DetalleRutina> detalles = detalleRutinaRepository.findAllByRutinaAndEliminadoFalse(rutina);
        rutinaDto.setDetalles(detalleRutinaMapper.toDtos(detalles));

        return rutinaDto;
    }
}
