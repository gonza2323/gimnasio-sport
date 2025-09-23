package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.ValorCuotaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.ValorCuotaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.CuotaMensualRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.ValorCuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/// falta agregar los msjs de error en messages.properties
@Service
@RequiredArgsConstructor
public class ValorCuotaService {
    private final ValorCuotaMapper valorCuotaMapper;
    private final ValorCuotaRepository valorCuotaRepository;
    private final CuotaMensualRepository cuotaMensualRepository;
    private final SocioRepository socioRepository;

    public ValorCuota crearValorCuota(ValorCuotaDto valorCuotaDto) {
        validar(valorCuotaDto);
        ValorCuota valorCuota = valorCuotaMapper.toEntity(valorCuotaDto);
        return valorCuotaRepository.save(valorCuota);
    }

    public ValorCuota buscarValorCuota(Long id) {
        return valorCuotaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("valorCuota.noEncontrado"));
    }

    public void eliminarValorCuota(Long id) {
        ValorCuota valorCuota = buscarValorCuota(id);
        valorCuota.setEliminado(true);
        valorCuotaRepository.save(valorCuota);
    }

    public ValorCuota modificarValorCuota(Long id, ValorCuotaDto valorCuotaDto) {
        ValorCuota valorCuotaExistente = buscarValorCuota(id);
        if (valorCuotaExistente.isEliminado()) {
            throw new BusinessException("valorCuota.eliminado.noModificable");
        }
        validar(valorCuotaDto);
        valorCuotaExistente.setFechaDesde(valorCuotaDto.getFechaDesde());
        valorCuotaExistente.setFechaHasta(valorCuotaDto.getFechaHasta());
        valorCuotaExistente.setValorCuota(valorCuotaDto.getValorCuota());
        return valorCuotaRepository.save(valorCuotaExistente);
    }

    public void validar(ValorCuotaDto valorCuotaDto) {
        if (valorCuotaDto.getFechaDesde() == null) {
            throw new BusinessException("fecha.desde.requerida");
        }
        if (valorCuotaDto.getFechaHasta() != null && valorCuotaDto.getFechaHasta().isBefore(valorCuotaDto.getFechaDesde())) {
            throw new BusinessException("fecha.hasta.menor.desde");
        }

        /// Si ya tengo un valor vigente de enero a marzo, no puedo crear otro que también incluya febrero
        boolean existeSolapado = valorCuotaRepository
                .existsByFechaDesdeLessThanEqualAndFechaHastaGreaterThanEqualAndEliminadoFalse(
                        valorCuotaDto.getFechaHasta(), valorCuotaDto.getFechaDesde()
                );

        if (existeSolapado) {
            throw new BusinessException("periodo.solapado");
        }
    }

    public List<ValorCuota> listarValoresCuota() {
        return valorCuotaRepository.findAll();
    }

    public List<ValorCuota> listarValoresCuotaActivos() {
        return valorCuotaRepository.findAllByEliminadoFalseOrderByFechaDesdeDesc();
    }

    public List<ValorCuotaDto> listarValoresCuotaDtosActivos() {
        List<ValorCuota> valoresCuota = valorCuotaRepository.findAllByEliminadoFalseOrderByFechaDesdeDesc();
        return valorCuotaMapper.toDtos(valoresCuota);
    }

    public CuotaMensual buscarValorCuotaVigente(Long numeroSocio) {
        Socio socio = socioRepository.findById(numeroSocio)
                .orElseThrow(() -> new BusinessException("socio.noEncontrado"));

        LocalDate hoy = LocalDate.now();

        return cuotaMensualRepository
                .findFirstBySocioAndEstadoAndFechaVencimientoGreaterThanEqualAndEliminadoFalse(
                        socio, EstadoCuota.ADEUDADA, hoy
                )
                .orElseThrow(() -> new BusinessException("cuota.noVigente"));
    }

}
