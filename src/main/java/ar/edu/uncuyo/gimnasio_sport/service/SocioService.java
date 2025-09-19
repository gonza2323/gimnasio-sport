package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.SocioCreateFormDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.mapper.PersonaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.PersonaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SocioService extends PersonaAbstractService<Socio> {
    private final SocioRepository socioRepository;

    public SocioService(PersonaRepository personaRepository, PersonaMapper mapper, DireccionService direccionService,
                        UsuarioService usuarioService, SucursalService sucursalService, SocioRepository socioRepository) {
        super(personaRepository, mapper, direccionService, usuarioService, sucursalService);
        this.socioRepository = socioRepository;
    }

    @Transactional
    public Socio crearSocio(SocioCreateFormDto socioCreateFormDto) {
        socioCreateFormDto.getPersona().getUsuario().setRol(RolUsuario.SOCIO);
        Socio socio = (Socio) crearPersona(socioCreateFormDto.getPersona());
        socio.setNumeroSocio(generarSiguienteNumeroDeSocio());
        socio.setEliminado(false);

        return socioRepository.save(socio);
    }

    private Long generarSiguienteNumeroDeSocio() {
        Long max = socioRepository.findMaxNumeroSocio();
        if (max == null) {
            return 1L;
        }
        return max + 1;
    }
}
