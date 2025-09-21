package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CuotaMensualMapper {
    CuotaMensual toEntity(CuotaMensualDto dto);
    CuotaMensualDto toDto(CuotaMensual entity);
    List<CuotaMensualDto> toDtos(List<CuotaMensual> entities);
}

