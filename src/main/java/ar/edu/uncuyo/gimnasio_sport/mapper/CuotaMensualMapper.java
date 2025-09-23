package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualCreateDto;
import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuotaMensualMapper {
    @Mapping(target = "valorCuota", ignore = true)
    @Mapping(target = "socio", ignore = true)
    @Mapping(target = "eliminado", ignore = true)
    CuotaMensual toEntity(CuotaMensualCreateDto dto);

    @Mapping(target = "monto", source = "valorCuota.valorCuota")
    CuotaMensualDto toDto(CuotaMensual entity);

    List<CuotaMensualDto> toDtos(List<CuotaMensual> entities);
}

