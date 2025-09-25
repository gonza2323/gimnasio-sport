package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.EmpleadoCreateForm;
import ar.edu.uncuyo.gimnasio_sport.dto.EmpleadoResumenDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.EmpleadoMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpleadoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;
    private final PersonaService personaService;
    private final EmpleadoMapper empleadoMapper;

    @Transactional
    public Empleado crearEmpleado(EmpleadoCreateForm empleadoCreateForm) {
        Empleado empleado = new Empleado();
        RolUsuario rol = determineRoleFromEmployeeType(empleadoCreateForm.getTipoEmpleado());
        empleadoCreateForm.getPersona().getUsuario().setRol(rol);
        personaService.crearPersona(empleado, empleadoCreateForm.getPersona());
        empleado.setTipoEmpleado(empleadoCreateForm.getTipoEmpleado());
        empleado.setEliminado(false);

        return empleadoRepository.save(empleado);
    }

    public Empleado eliminarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado con id: " + id));
        empleado.setEliminado(true);
        return empleadoRepository.save(empleado);
    }

    private RolUsuario determineRoleFromEmployeeType(TipoEmpleado tipoEmpleado) {
        return switch (tipoEmpleado) {
            case PROFESOR -> RolUsuario.PROFESOR;
            case ADMINISTRATIVO -> RolUsuario.ADMINISTRATIVO;
            default -> throw new BusinessException("Rol no definido para este tipo de empleado");
        };
    }

    @Transactional
    public List<EmpleadoResumenDto> listarEmpleadoResumenDtos() {
        List<Empleado> empleados = empleadoRepository.findAllByEliminadoFalse();
        return empleadoMapper.toResumenDtos(empleados);
    }

    public Empleado buscarEmpleadoActual() {
        Persona persona = personaService.buscarPersonaActual();

        if (!(persona instanceof Empleado))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return (Empleado) persona;
    }

    public Empleado buscarProfesorActual() {
        Empleado empleado = buscarEmpleadoActual();
        if (empleado.getTipoEmpleado() != TipoEmpleado.PROFESOR)
            throw new BusinessException("Invalido.rutina.profesor");

        return empleado;
    }
}
