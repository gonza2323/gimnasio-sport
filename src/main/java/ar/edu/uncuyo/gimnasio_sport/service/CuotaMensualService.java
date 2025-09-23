package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.CuotaMensualMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.CuotaMensualRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.ValorCuotaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuotaMensualService {
    private final CuotaMensualRepository cuotaMensualRepository;
    private final CuotaMensualMapper cuotaMensualMapper;
    private final ValorCuotaRepository valorCuotaRepository;
    private final SocioService socioService;
    private final ValorCuotaService valorCuotaService;

    public CuotaMensual crearCuotaMensual(CuotaMensualDto cuotaMensualDto) throws BusinessException {
        if (cuotaMensualRepository.existsBySocioIdAndMesAndAnioAndEliminadoFalse(
                cuotaMensualDto.getSocioId(),
                cuotaMensualDto.getMes(),
                cuotaMensualDto.getAnio())) {
            throw new BusinessException("socio.cuota.existe");
        }

        Socio socio = socioService.buscarSocio(cuotaMensualDto.getSocioId());

        CuotaMensual cuota = cuotaMensualMapper.toEntity(cuotaMensualDto);
        cuota.setSocio(socio);

        return cuotaMensualRepository.save(cuota);
    }

    @Transactional
    public long emitirCuotasMesActual() {
        LocalDate fechaActual = LocalDate.now();
        Month mes = fechaActual.getMonth();
        Long anio = (long) fechaActual.getYear();
        List<Socio> sociosSinCuota = socioService.buscarSociosSinCuotaMesYAnioActual(mes, anio);
        ValorCuota valorCuota = valorCuotaService.buscarValorCuotaVigente();
        
        List<CuotaMensual> cuotasMensuales = sociosSinCuota.stream().map(socio ->
            CuotaMensual.builder()
                    .mes(LocalDate.now().getMonth())
                    .anio(anio)
                    .estado(EstadoCuota.ADEUDADA)
                    .fechaVencimiento(fechaActual.plusDays(10))
                    .socio(socio)
                    .valorCuota(valorCuota)
                    .eliminado(false)
                    .build()
        ).toList();

        cuotaMensualRepository.saveAll(cuotasMensuales);
        return (long) cuotasMensuales.size();
    }
    
    public CuotaMensual buscarCuotaMensual(Long id) throws BusinessException {
        return cuotaMensualRepository.findById(id)
                .orElseThrow(() -> new BusinessException("cuota.no.existe"));
    }

    public CuotaMensual modificarCuotaMensual(Long id, CuotaMensualDto cuotaMensualDto) throws BusinessException {
        CuotaMensual cuotaExistente = buscarCuotaMensual(id);

        if (!cuotaExistente.getMes().equals(cuotaMensualDto.getMes()) ||
            !cuotaExistente.getAnio().equals(cuotaMensualDto.getAnio())) {
            if (cuotaMensualRepository.existsBySocioIdAndMesAndAnioAndEliminadoFalse(
                    cuotaExistente.getSocio().getId(),
                    cuotaMensualDto.getMes(),
                    cuotaMensualDto.getAnio())) {
                throw new BusinessException("socio.cuota.existe");
            }
        }

        cuotaExistente.setMes(cuotaMensualDto.getMes());
        cuotaExistente.setAnio(cuotaMensualDto.getAnio());
        cuotaExistente.setEstado(cuotaMensualDto.getEstado());
        cuotaExistente.setFechaVencimiento(cuotaMensualDto.getFechaVencimiento());

        return cuotaMensualRepository.save(cuotaExistente);
    }

    public void eliminarCuotaMensual(Long id) {
        CuotaMensual cuotaExistente = buscarCuotaMensual(id);
        cuotaExistente.setEliminado(true);
        cuotaMensualRepository.save(cuotaExistente);
    }

    public List<CuotaMensualDto> listarCuotaMensual() {
        List<CuotaMensual> cuotas = cuotaMensualRepository.findAll();
        return cuotaMensualMapper.toDtos(cuotas);
    }

    public List<CuotaMensualDto> listarCuotaMensualActivo() {
        List<CuotaMensual> cuotas = cuotaMensualRepository.findAllByEliminadoFalse();
        return cuotaMensualMapper.toDtos(cuotas);
    }

    public List<CuotaMensualDto> listarCuotasPorEstado(EstadoCuota estado) {
        List<CuotaMensual> cuotas = cuotaMensualRepository.findAllByEliminadoFalseAndEstado(estado);
        return cuotaMensualMapper.toDtos(cuotas);
    }

    public List<CuotaMensualDto> listarCuotasPorFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        List<CuotaMensual> cuotas = cuotaMensualRepository.findAllByFechaVencimientoBetween(fechaDesde, fechaHasta);
        return cuotaMensualMapper.toDtos(cuotas);
    }


    public void validar(Month mes, Long anio, Long idValorCuota) {
        if (mes == null) {
            throw new BusinessException("El mes no puede ser nulo");
        }

        if (anio == null || anio < 2000 || anio > 2100) {
            throw new BusinessException("El año es inválido");
        }

        if (!valorCuotaRepository.existsById(idValorCuota)) {
            throw new BusinessException("El valor de cuota no existe");
        }
    }
}
