package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpleadoRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.RutinaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RutinaServiceImpl implements RutinaService {

    private static final long DURACION_MINIMA_MS = TimeUnit.DAYS.toMillis(1);
    private static final long DURACION_MAXIMA_MS = TimeUnit.DAYS.toMillis(365);

    private final RutinaRepository rutinaRepository;
    private final SocioRepository socioRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    @Transactional
    public RutinaDto crear(RutinaDto dto) {
        validarRutina(dto);
        Rutina rutina = new Rutina();
        rutina.setEliminado(false);
        aplicarDatos(rutina, dto);
        return toDto(rutinaRepository.save(rutina));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaDto> listar() {
        return rutinaRepository.findAllByEliminadoFalse()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RutinaDto buscarPorId(Long id) {
        Rutina rutina = obtenerRutinaActiva(id);
        return toDto(rutina);
    }

    @Override
    @Transactional
    public RutinaDto actualizar(Long id, RutinaDto dto) {
        validarRutina(dto);
        Rutina rutina = obtenerRutinaActiva(id);
        aplicarDatos(rutina, dto);
        return toDto(rutinaRepository.save(rutina));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Rutina rutina = obtenerRutinaActiva(id);
        rutina.setEliminado(true);
        rutinaRepository.save(rutina);
    }

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

        if (dto.getSocioId() == null) {
            throw new BusinessException("rutina.socio.requerido");
        }
        if (dto.getProfesorId() == null) {
            throw new BusinessException("rutina.profesor.requerido");
        }
    }

    private void aplicarDatos(Rutina rutina, RutinaDto dto) {
        rutina.setTipo(dto.getTipo());
        rutina.setFechaInicio(dto.getFechaInicio());
        rutina.setFechaFinalizacion(dto.getFechaFinalizacion());
        rutina.setUsuario(obtenerSocio(dto.getSocioId()));
        rutina.setProfesor(obtenerProfesor(dto.getProfesorId()));
    }

    private Rutina obtenerRutinaActiva(Long id) {
        return rutinaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("rutina.noEncontrada"));
    }

    private Socio obtenerSocio(Long socioId) {
        return socioRepository.findById(socioId)
                .orElseThrow(() -> new BusinessException("rutina.socio.noEncontrado"));
    }

    private Empleado obtenerProfesor(Long profesorId) {
        return empleadoRepository.findById(profesorId)
                .orElseThrow(() -> new BusinessException("rutina.profesor.noEncontrado"));
    }

    private RutinaDto toDto(Rutina rutina) {
        return new RutinaDto(
                rutina.getId(),
                rutina.getTipo(),
                rutina.getFechaInicio(),
                rutina.getFechaFinalizacion(),
                rutina.getUsuario() != null ? rutina.getUsuario().getId() : null,
                rutina.getProfesor() != null ? rutina.getProfesor().getId() : null
        );
    }
}

