package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Pais;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.PaisMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.PaisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaisService {
    private final PaisRepository paisRepository;
    private final PaisMapper paisMapper;

    @Transactional
    public PaisDto buscarPaisDto(Long id) {
        Pais pais = buscarPais(id);
        return paisMapper.toDto(pais);
    }

    @Transactional
    public List<PaisDto> listarPaisesDtos() {
        List<Pais> paises = paisRepository.findAllByEliminadoFalseOrderByNombre();
        return paisMapper.toDtos(paises);
    }

    @Transactional
    public void crearPais(PaisDto paisDto) {
        if (paisRepository.existsByNombreAndEliminadoFalse(paisDto.getNombre()))
            throw new BusinessException("yaExiste.pais.nombre");

        Pais pais = paisMapper.toEntity(paisDto);
        pais.setId(null);
        paisRepository.save(pais);
    }

    @Transactional
    public void modificarPais(PaisDto paisDto) {
        Pais pais = buscarPais(paisDto.getId());

        if (paisRepository.existsByNombreAndIdNotAndEliminadoFalse(paisDto.getNombre(), paisDto.getId()))
            throw new BusinessException("yaExiste.pais.nombre");

        paisMapper.updateEntityFromDto(paisDto, pais);
        paisRepository.save(pais);
    }

    @Transactional
    public void eliminarPais(Long id) {
        Pais pais = buscarPais(id);
        pais.setEliminado(true);
        paisRepository.save(pais);
    }

    public Pais buscarPais(Long id) {
        return paisRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("noExiste.pais"));
    }
}
