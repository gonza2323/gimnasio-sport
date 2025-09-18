package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.dto.ProvinciaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import ar.edu.uncuyo.gimnasio_sport.entity.Provincia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinciaMapper {

    @Mapping(target = "pais", ignore = true)
    Provincia toEntity(ProvinciaDto dto);

    @Mapping(target = "paisId", source = "pais.id")
    ProvinciaDto toDto(Provincia province);

    List<ProvinciaDto> toDtos(List<Provincia> provincias);

    @Mapping(target = "pais", ignore = true)
    void updateEntityFromDto(ProvinciaDto dto, @MappingTarget Provincia provincia);
}
