package ar.edu.uncuyo.gimnasio_sport.mapper;

import ar.edu.uncuyo.gimnasio_sport.dto.UsuarioCreateFormDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    Usuario toEntity(UsuarioCreateFormDTO dto);
    UsuarioCreateFormDTO toDto(Usuario usuario);
    List<UsuarioCreateFormDTO> toDtos(List<Usuario> usuario);
    void updateEntityFromDto(UsuarioCreateFormDTO dto, @MappingTarget Usuario usuario);
}
