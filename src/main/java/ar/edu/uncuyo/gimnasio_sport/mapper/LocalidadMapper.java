package ar.edu.uncuyo.gimnasio_sport.mapper;
import ar.edu.uncuyo.gimnasio_sport.dto.LocalidadDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Localidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalidadMapper {

    @Mapping(target = "departamento", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Localidad toEntity(LocalidadDto dto);

    @Mapping(target = "departamentoId", source = "departamento.id")
    LocalidadDto toDto(Localidad localidad);

    List<LocalidadDto> toDtos(List<Localidad> localidades);
}
