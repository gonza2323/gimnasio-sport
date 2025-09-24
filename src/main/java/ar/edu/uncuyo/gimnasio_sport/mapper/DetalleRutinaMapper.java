package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleRutina;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleRutinaMapper {

    @Mapping(target = "fecha", expression = "java(cloneDate(dto.getFecha()))")
    @Mapping(target = "rutina", ignore = true)
    @Mapping(target = "estadoRutina", ignore = true)
    DetalleRutina toEntity(DetalleRutinaDto dto);

    @Mapping(target = "fecha", expression = "java(cloneDate(detalle.getFecha()))")
    @Mapping(target = "rutinaId", expression = "java(detalle.getRutina() != null ? detalle.getRutina().getId() : null)")
    DetalleRutinaDto toDto(DetalleRutina detalle);

    List<DetalleRutina> toEntities(List<DetalleRutinaDto> detalles);

    List<DetalleRutinaDto> toDtos(List<DetalleRutina> detalles);

    @Mapping(target = "fecha", expression = "java(cloneDate(dto.getFecha()))")
    @Mapping(target = "rutina", ignore = true)
    void updateFromDto(DetalleRutinaDto dto, @MappingTarget DetalleRutina detalle);

    default Date cloneDate(Date source) {
        return source == null ? null : new Date(source.getTime());
    }
}

