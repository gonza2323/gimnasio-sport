package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.PersonaCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.UsuarioCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.mapper.PersonaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class PersonaAbstractService {
    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final DireccionService direccionService;
    private final UsuarioService usuarioService;
    private final SucursalService sucursalService;

    @Transactional
    protected void setDatosPersona(Persona persona, PersonaCreateFormDTO formDto) {
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
}
