package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.SucursalDto;
import ar.edu.uncuyo.gimnasio_sport.dto.SucursalResumenDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DireccionMapper.class})
public interface SucursalMapper {

    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    Sucursal toEntity(SucursalDto dto);

    @Mapping(target = "direccion", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    void updateEntityFromDto(SucursalDto dto, @MappingTarget Sucursal sucursal);

    SucursalDto toDto(Sucursal sucursal);

    @Mapping(target = "nombreProvincia", source = "direccion.localidad.departamento.provincia.nombre")
    @Mapping(target = "nombrePais", source = "direccion.localidad.departamento.provincia.pais.nombre")
    SucursalResumenDTO toSummaryDto(Sucursal sucursal);

    List<SucursalResumenDTO> toSummaryDtos(List<Sucursal> sucursales);
}
