package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.ProvinciaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.ProvinciaListaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import ar.edu.uncuyo.gimnasio_sport.entity.Provincia;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.ProvinciaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.ProvinciaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinciaService {
    private final ProvinciaRepository provinciaRepository;
    private final PaisService paisService;
    private final ProvinciaMapper provinciaMapper;

    @Transactional
    public ProvinciaDto buscarProvinciaDto(Long id) {
        Provincia provincia = buscarProvincia(id);
        return provinciaMapper.toDto(provincia);
    }

    @Transactional
    public List<ProvinciaListaDto> listarProvinciasDtos() {
        List<Provincia> provincias = provinciaRepository.findAllByEliminadoFalseOrderByNombre();
        return provinciaMapper.toSummaryDtos(provincias);
    }

    @Transactional
    public void crearProvincia(ProvinciaDto provinciaDto) {
        if (provinciaRepository.existsByNombreAndEliminadoFalse((provinciaDto.getNombre())))
            throw new BusinessException("YaExiste.provincia.nombre");

        Pais pais = paisService.buscarPais(provinciaDto.getPaisId());

        Provincia provincia = provinciaMapper.toEntity(provinciaDto);
        provincia.setId(null);
        provincia.setPais(pais);
        provincia.setEliminado(false);
        provinciaRepository.save(provincia);
    }

    @Transactional
    public void modificarProvincia(ProvinciaDto provinciaDto) {
        Provincia provincia = buscarProvincia(provinciaDto.getId());

        if (provinciaRepository.existsByNombreAndIdNotAndEliminadoFalse(provinciaDto.getNombre(), provinciaDto.getId()))
            throw new BusinessException("YaExiste.provincia.nombre");

        Pais pais = paisService.buscarPais(provinciaDto.getPaisId());

        provinciaMapper.updateEntityFromDto(provinciaDto, provincia);
        provincia.setPais(pais);
        provinciaRepository.save(provincia);
    }

    @Transactional
    public void eliminarProvincia(Long id) {
        Provincia provincia = buscarProvincia(id);
        provincia.setEliminado(true);
        provinciaRepository.save(provincia);
    }

    public Provincia buscarProvincia(Long id) {
        return provinciaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NoExiste.provincia"));
    }

    public List<ProvinciaDto> buscarProvinciaPorPais(Long paisId) {
        List<Provincia> provincias = provinciaRepository.findAllByPaisIdAndEliminadoFalse(paisId);
        return provinciaMapper.toDtos(provincias);
    }
}
