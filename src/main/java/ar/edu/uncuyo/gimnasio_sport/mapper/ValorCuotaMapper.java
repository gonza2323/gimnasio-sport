package ar.edu.uncuyo.gimnasio_sport.mapper;


import ar.edu.uncuyo.gimnasio_sport.dto.ValorCuotaDto;

import ar.edu.uncuyo.gimnasio_sport.entity.ValorCuota;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ValorCuotaMapper {
    ValorCuota toEntity(ValorCuotaDto dto);
    ValorCuotaDto toDto(ValorCuota entity);
    List<ValorCuotaDto> toDtos(List<ValorCuota> entities);

}
