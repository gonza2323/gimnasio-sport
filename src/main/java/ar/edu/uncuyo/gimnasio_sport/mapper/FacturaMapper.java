package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.FacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Factura;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DetalleFacturaMapper.class})
public interface FacturaMapper {

    Factura toEntity(FacturaDto dto);

    FacturaDto toDto(Factura factura);

    List<FacturaDto> toDtos(List<Factura> facturas);

    void updateFromDto(FacturaDto dto, @MappingTarget Factura factura);
}

