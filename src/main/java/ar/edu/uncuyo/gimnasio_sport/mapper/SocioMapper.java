package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.SocioCreateFormDto;
import ar.edu.uncuyo.gimnasio_sport.dto.SocioResumenDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SocioMapper {
    @Mapping(target = "nombreSucursal", source = "sucursal.nombre")
    SocioResumenDto toSummaryDto(Socio socio);

    List<SocioResumenDto> toSummaryDtos(List<Socio> socios);

    SocioCreateFormDto toDto(Socio socio);
}