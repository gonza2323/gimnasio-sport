package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.EmpleadoCreateForm;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.PersonaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpleadoRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.PersonaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService extends PersonaAbstractService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(PersonaRepository personaRepository, PersonaMapper mapper, DireccionService direccionService,
                           UsuarioService usuarioService, SucursalService sucursalService, EmpleadoRepository empleadoRepository) {
        super(personaRepository, mapper, direccionService, usuarioService, sucursalService);
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Empleado crearEmpleado(EmpleadoCreateForm empleadoCreateForm) {
        Empleado empleado = new Empleado();
        RolUsuario rol = determineRoleFromEmployeeType(empleadoCreateForm.getTipoEmpleado());
        empleadoCreateForm.getPersona().getUsuario().setRol(rol);
        setDatosPersona(empleado, empleadoCreateForm.getPersona());
        empleado.setTipoEmpleado(empleadoCreateForm.getTipoEmpleado());
        empleado.setEliminado(false);

        return empleadoRepository.save(empleado);
    }

    private RolUsuario determineRoleFromEmployeeType(TipoEmpleado tipoEmpleado) {
        return switch (tipoEmpleado) {
            case PROFESOR -> RolUsuario.PROFESOR;
            case ADMINISTRATIVO -> RolUsuario.ADMINISTRATIVO;
            default -> throw new BusinessException("Rol no definido para este tipo de empleado");
        };
    }
}
