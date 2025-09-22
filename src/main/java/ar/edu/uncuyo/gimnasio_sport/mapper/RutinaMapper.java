package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Date;

@Mapper(componentModel = "spring", uses = {DetalleRutinaMapper.class})
public interface RutinaMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "detalles", source = "detalles")
    @Mapping(target = "fechaInicio", expression = "java(cloneDate(dto.getFechaInicio()))")
    @Mapping(target = "fechaFinalizacion", expression = "java(cloneDate(dto.getFechaFinalizacion()))")
    Rutina toEntity(RutinaDto dto);

    @Mapping(target = "fechaInicio", expression = "java(cloneDate(rutina.getFechaInicio()))")
    @Mapping(target = "fechaFinalizacion", expression = "java(cloneDate(rutina.getFechaFinalizacion()))")
    @Mapping(target = "socioId", expression = "java(rutina.getUsuario() != null ? rutina.getUsuario().getId() : null)")
    @Mapping(target = "profesorId", expression = "java(rutina.getProfesor() != null ? rutina.getProfesor().getId() : null)")
    @Mapping(target = "detalles", source = "detalles")
    RutinaDto toDto(Rutina rutina);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "tipo", source = "tipo")
    @Mapping(target = "fechaInicio", expression = "java(cloneDate(dto.getFechaInicio()))")
    @Mapping(target = "fechaFinalizacion", expression = "java(cloneDate(dto.getFechaFinalizacion()))")
    void updateEntityFromDto(RutinaDto dto, @MappingTarget Rutina rutina);

    default Date cloneDate(Date source) {
        return source == null ? null : new Date(source.getTime());
    }
}
