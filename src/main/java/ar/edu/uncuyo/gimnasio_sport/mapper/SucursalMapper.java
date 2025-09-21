package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.SucursalResumenDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SucursalMapper {

    @Mapping(target = "nombreProvincia", source = "direccion.localidad.departamento.provincia.nombre")
    @Mapping(target = "nombrePais", source = "direccion.localidad.departamento.provincia.pais.nombre")
    SucursalResumenDTO toSummaryDto(Sucursal sucursal);

    List<SucursalResumenDTO> toSummaryDtos(List<Sucursal> sucursales);
}
