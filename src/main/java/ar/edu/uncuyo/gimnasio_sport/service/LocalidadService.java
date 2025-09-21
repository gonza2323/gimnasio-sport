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

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalidadService {
    private final LocalidadRepository localidadRepository;
    private final DepartamentoService departamentoService;
    private final LocalidadMapper localidadMapper;

    @Transactional
    public void crearLocalidad(LocalidadDto localidadDto) {
        if (localidadRepository.existsByNombreAndEliminadoFalse((localidadDto.getNombre())))
            throw new BusinessException("YaExiste.localidad.nombre");

        Departamento departamento = departamentoService.buscarDepartamento(localidadDto.getDepartamentoId());

        Localidad localidad = localidadMapper.toEntity(localidadDto);
        localidad.setId(null);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);
        localidadRepository.save(localidad);
    }

    public Localidad buscarLocalidad(Long id) {
        return localidadRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NoExiste.localidad"));
    }

    public List<LocalidadDto> buscarLocalidadesPorDepartamento(Long departamentoId) {
        List<Localidad> localidades = localidadRepository.findAllByDepartamentoIdAndEliminadoFalse(departamentoId);
        return localidadMapper.toDtos(localidades);
    }
}
