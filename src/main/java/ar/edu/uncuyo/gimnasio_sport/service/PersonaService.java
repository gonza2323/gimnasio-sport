package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.auth.CustomUserDetails;
import ar.edu.uncuyo.gimnasio_sport.dto.PersonaCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.UsuarioCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.PersonaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final DireccionService direccionService;
    private final UsuarioService usuarioService;
    private final SucursalService sucursalService;

    @Transactional
    protected void crearPersona(Persona persona, PersonaCreateFormDTO formDto) {
        UsuarioCreateFormDTO usuarioDto = formDto.getUsuario();
        usuarioDto.setNombreUsuario(formDto.getCorreoElectronico());
        Usuario usuario = usuarioService.crearUsuario(usuarioDto);

        Direccion direccion = direccionService.crearDireccion(formDto.getDireccion());

        Sucursal sucursal = sucursalService.buscarSucursal(formDto.getSucursalId());

        personaMapper.updateEntityFromDto(formDto, persona);
        persona.setSucursal(sucursal);
        persona.setUsuario(usuario);
        persona.setDireccion(direccion);
        persona.setId(null);
        persona.setEliminado(false);
    }

    protected void eliminarPersona(Long id) {
        Persona persona = buscarPersona(id);
        direccionService.eliminarDireccion(persona.getDireccion());
        persona.setEliminado(true);
        personaRepository.save(persona);
    }

    protected Persona buscarPersona(Long id) {
        return personaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NotFound.persona"));
    }

    public Persona buscarPersonaActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return personaRepository.findByUsuarioIdAndEliminadoFalse(userDetails.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
