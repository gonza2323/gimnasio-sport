package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleFacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleFactura;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleFacturaMapper {

    DetalleFactura toEntity(DetalleFacturaDto dto);

    DetalleFacturaDto toDto(DetalleFactura detalleFactura);

    List<DetalleFacturaDto> toDtos(List<DetalleFactura> detallesFacturas);

    void updateFromDto(DetalleFacturaDto dto, @MappingTarget DetalleFactura detallesFacturas);
}

