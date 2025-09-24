package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DetalleRutinaMapper.class})
public interface RutinaMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "detalles", source = "detalles")
    Rutina toEntity(RutinaDto dto);

    @Mapping(target = "socioId", expression = "java(rutina.getUsuario() != null ? rutina.getUsuario().getId() : null)")
    @Mapping(target = "profesorId", expression = "java(rutina.getProfesor() != null ? rutina.getProfesor().getId() : null)")
    @Mapping(target = "detalles", source = "detalles")
    @Mapping(target = "socioNombre", ignore = true)
    @Mapping(target = "socioEmail", ignore = true)
    @Mapping(target = "socioNumero", ignore = true)
    @Mapping(target = "profesorNombre", ignore = true)
    RutinaDto toDto(Rutina rutina);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "tipo", source = "tipo")
    void updateEntityFromDto(RutinaDto dto, @MappingTarget Rutina rutina);
}
