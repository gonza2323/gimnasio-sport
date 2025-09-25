package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Rutina;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DetalleRutinaMapper.class})
public interface RutinaMapper {

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    Rutina toEntity(RutinaDto dto);

    @Mapping(target = "socioId", source = "usuario.id")
    @Mapping(target = "profesorId", source = "profesor.id")
    @Mapping(target = "detalles", source = "detalles")
    @Mapping(target = "socioNombre", source = "usuario.nombre")
    @Mapping(target = "socioApellido", source = "usuario.apellido")
    @Mapping(target = "socioEmail", ignore = true)
    @Mapping(target = "socioNumero", ignore = true)
    @Mapping(target = "profesorNombre", source = "profesor.nombre")
    @Mapping(target = "profesorApellido", source = "profesor.apellido")
    RutinaDto toDto(Rutina rutina);

    List<RutinaDto> toDtos(List<Rutina> rutina);

    @BeanMapping(ignoreByDefault = true)
    void updateEntityFromDto(RutinaDto dto, @MappingTarget Rutina rutina);
}
