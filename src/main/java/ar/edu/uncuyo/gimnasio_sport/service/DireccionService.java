package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DireccionDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Localidad;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import ar.edu.uncuyo.gimnasio_sport.entity.Provincia;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.error.FieldSpecificBusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.DireccionMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;
    private final DireccionMapper direccionMapper;
    private final LocalidadService localidadService;

    @Transactional
    public DireccionDto buscarDireccionDto(Long id) {
        Direccion direccion = buscarDireccion(id);
        return direccionMapper.toDto(direccion);
    }

    @Transactional
    public void crearDireccion(DireccionDto direccionDto) {
        Localidad localidad;
        try {
            localidad = localidadService.buscarLocalidad(direccionDto.getLocalidadId());
        } catch (BusinessException e) {
            throw new FieldSpecificBusinessException("localidadId", "noExiste");
        }

        Direccion direccion = direccionMapper.toEntity(direccionDto);
        direccion.setId(null);
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        direccionRepository.save(direccion);
    }

    @Transactional
    public void modificarDireccion(DireccionDto direccionDto) {
        Direccion direccion = buscarDireccion(direccionDto.getId());

        Localidad localidad;
        try {
            localidad = localidadService.buscarLocalidad(direccionDto.getLocalidadId());
        } catch (BusinessException e) {
            throw new FieldSpecificBusinessException("localidadId", "noExiste");
        }

        direccionMapper.updateEntityFromDto(direccionDto, direccion);
        direccion.setLocalidad(localidad);
        direccionRepository.save(direccion);
    }

    @Transactional
    public void eliminarDireccion(Long id) {
        Direccion direccion = buscarDireccion(id);
        direccion.setEliminado(true);
        direccionRepository.save(direccion);
    }

    public Direccion buscarDireccion(Long id) {
        return direccionRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("noExiste.direccion"));
    }
}
