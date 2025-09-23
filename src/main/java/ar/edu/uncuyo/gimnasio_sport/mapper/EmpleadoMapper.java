package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.EmpleadoCreateForm;
import ar.edu.uncuyo.gimnasio_sport.dto.EmpleadoResumenDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpleadoMapper {

    // --- Entity ↔ EmpleadoCreateForm ---
    Empleado toEntity(EmpleadoCreateForm form);
    EmpleadoCreateForm toForm(Empleado entity);

    // --- Entity ↔ EmpleadoResumenDto (para listados) ---
    @Mapping(target = "tipoEmpleado", source = "tipoEmpleado")      // Enum → String automático
    @Mapping(target = "sucursalNombre", source = "sucursal.nombre") // Relación con sucursal
    EmpleadoResumenDto toResumenDto(Empleado entity);

    List<EmpleadoResumenDto> toResumenDtos(List<Empleado> entities);

    // --- Update para editar empleados ---
    void updateFromForm(EmpleadoCreateForm form, @MappingTarget Empleado entity);
}
