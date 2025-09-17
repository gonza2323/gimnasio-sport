package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaisMapper {
    Pais toEntity(PaisDto dto);
    PaisDto toDto(Pais pais);
    List<PaisDto> toDtos(List<Pais> paises);
    void updateEntityFromDto(PaisDto dto, @MappingTarget Pais pais);
}
