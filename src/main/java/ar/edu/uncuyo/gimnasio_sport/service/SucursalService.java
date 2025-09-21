package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.SucursalDto;
import ar.edu.uncuyo.gimnasio_sport.dto.SucursalResumenDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.SucursalMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SucursalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


// TODO: SucursalService debería aceptar los datos de dirección y crearla llamando a SucursalService
@Service
@RequiredArgsConstructor
public class SucursalService {
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final DireccionService direccionService;
    private final SucursalMapper sucursalMapper;
    private final EmpresaService empresaService;

    @Transactional
    public Sucursal crearSucursal(SucursalDto formDto) {
        if (sucursalRepository.existsByNombreAndEliminadoFalse(formDto.getNombre())) {
            throw new BusinessException("YaExiste.sucursal.nombre");
        }

        Direccion direccion = direccionService.crearDireccion(formDto.getDireccion());

        // TODO: Asumimos que hay una sola empresa, no pienso hacer un ABM para una sola empresa
        Empresa empresa = empresaService.buscarEmpresa(1L);

        Sucursal sucursal = sucursalMapper.toEntity(formDto);
        sucursal.setEmpresa(empresa);
        sucursal.setDireccion(direccion);
        sucursal.setEliminado(false);

        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public void modificarSucursal(SucursalDto formDto){
        Sucursal sucursal = buscarSucursal(formDto.getId());

        if (sucursalRepository.existsByNombreAndIdNotAndEliminadoFalse(formDto.getNombre(), formDto.getId()))
            throw new BusinessException("YaExiste.sucursal.nombre");

        sucursalMapper.updateEntityFromDto(formDto, sucursal);
        direccionService.modificarDireccion(formDto.getDireccion());

        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminarSucursal(Long id){
        Sucursal sucursal = buscarSucursal(id);
        sucursal.setEliminado(true);
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("NotFound.sucursal"));
    }

    @Transactional
    public List<SucursalResumenDTO> listarSucursalResumenDto() {
        List<Sucursal> sucursales = sucursalRepository.findAllByEliminadoFalse();
        return sucursalMapper.toSummaryDtos(sucursales);
    }

    @Transactional
    public SucursalDto buscarSucursalDto(Long id) {
        return sucursalMapper.toDto(buscarSucursal(id));
    }
}
