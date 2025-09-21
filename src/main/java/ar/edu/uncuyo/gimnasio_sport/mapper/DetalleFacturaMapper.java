package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleFacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleFactura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleFacturaMapper {

    @Mapping(target = "facturaId", source = "factura.id")
    DetalleFacturaDto toDto(DetalleFactura entity);

    @Mapping(target = "factura", ignore = true) // la factura se asigna en el servicio
    DetalleFactura toEntity(DetalleFacturaDto dto);

    List<DetalleFacturaDto> toDtos(List<DetalleFactura> detallesFacturas);

    void updateFromDto(DetalleFacturaDto dto, @MappingTarget DetalleFactura detallesFacturas);
}

