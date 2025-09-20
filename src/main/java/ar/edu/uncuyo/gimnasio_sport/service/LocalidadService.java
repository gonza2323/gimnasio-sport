package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.LocalidadDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Departamento;
import ar.edu.uncuyo.gimnasio_sport.entity.Localidad;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.LocalidadMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.LocalidadRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalidadService {
    private final LocalidadRepository localidadRepository;
    private final DepartamentoService departamentoService;
    private final LocalidadMapper localidadMapper;

    @Transactional
    public LocalidadDto buscarDepartamentoDto(Long id) {
        Localidad localidad = buscarLocalidad(id);
        return localidadMapper.toDto(localidad);
    }

    @Transactional
    public void crearLocalidad(LocalidadDto localidadDto) {
        if (localidadRepository.existsByNombreAndEliminadoFalse((localidadDto.getNombre())))
            throw new BusinessException("yaExiste.provincia.nombre");

        Departamento departamento;
        try {
            departamento = departamentoService.buscarDepartamento(localidadDto.getDepartamentoId());
        } catch (BusinessException e) {
            throw new BusinessException("noExiste.provincia");
        }

        Localidad localidad = localidadMapper.toEntity(localidadDto);
        localidad.setId(null);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        localidadRepository.save(localidad);
    }

    public Localidad buscarLocalidad(Long id) {
        return localidadRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("noExiste.localidad"));
    }
}
