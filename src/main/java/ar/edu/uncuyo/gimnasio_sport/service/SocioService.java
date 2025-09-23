package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.SocioCreateFormDto;
import ar.edu.uncuyo.gimnasio_sport.dto.SocioResumenDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.SocioMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocioService {
    private final SocioRepository socioRepository;
    private final SocioMapper socioMapper;
    private final PersonaService personaService;

    @Transactional
    public Socio crearSocio(SocioCreateFormDto socioCreateFormDto) {
        Socio socio = new Socio();
        socioCreateFormDto.getUsuario().setRol(RolUsuario.SOCIO);
        personaService.crearPersona(socio, socioCreateFormDto);
        socio.setNumeroSocio(generarSiguienteNumeroDeSocio());
        socio.setEliminado(false);

        return socioRepository.save(socio);
    }

    @Transactional
    public List<SocioResumenDto> listarSocioResumenDtos() {
        List<Socio> socios = socioRepository.findAllByEliminadoFalse();
        return socioMapper.toSummaryDtos(socios);
    }

    public void eliminarSocio(Long id) {
        personaService.eliminarPersona(id);
    }

    private Long generarSiguienteNumeroDeSocio() {
        Long max = socioRepository.findMaxNumeroSocio();
        return max == null ? 1L : max + 1;
    }

    public SocioCreateFormDto buscarSocioDto(Long id) {
        Socio socio = buscarSocio(id);
        return socioMapper.toDto(socio);
    }

    public Socio buscarSocio(Long id) {
        return socioRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NotFound.socio"));
    }

    public List<Socio> buscarSociosSinCuotaMesYAnioActual(Month mes, Long anio) {
        return socioRepository.buscarSociosSinCuotaMesYAnioActual(mes, anio);
    }

    public Socio buscarSocioActual() {
        Persona persona = personaService.buscarPersonaActual();

        if (!(persona instanceof Socio))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return (Socio) persona;
    }
}
