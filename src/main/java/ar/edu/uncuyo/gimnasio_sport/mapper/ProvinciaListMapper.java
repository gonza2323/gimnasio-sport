package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.ProvinciaListaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Provincia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProvinciaListMapper {

    @Mapping(target = "paisNombre", source = "pais.nombre")
    ProvinciaListaDto toDto(Provincia province);

    List<ProvinciaListaDto> toDtos(List<Provincia> provincias);
}
