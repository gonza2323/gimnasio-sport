package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleFacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleFactura;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CuotaMensualMapper.class})
public interface DetalleFacturaMapper {

    @Mapping(target = "factura", ignore = true)
    @Mapping(target = "cuotaMensual", ignore = true)
    DetalleFactura toEntity(DetalleFacturaDto dto);

    List<DetalleFactura> toEntities(List<DetalleFacturaDto> detalles);

    @Mapping(target = "facturaId", source = "factura.id")
    @Mapping(target = "cuota", source = "cuotaMensual")
    DetalleFacturaDto toDto(DetalleFactura entity);

    List<DetalleFacturaDto> toDtos(List<DetalleFactura> detallesFacturas);

    void updateFromDto(DetalleFacturaDto dto, @MappingTarget DetalleFactura detallesFacturas);
}

