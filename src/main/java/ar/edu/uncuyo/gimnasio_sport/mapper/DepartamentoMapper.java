package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.DepartamentoDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Departamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

    @Mapping(target = "provincia", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    Departamento toEntity(DepartamentoDto dto);

    @Mapping(target = "provinciaId", source = "provincia.id")
    DepartamentoDto toDto(Departamento departamento);
}
