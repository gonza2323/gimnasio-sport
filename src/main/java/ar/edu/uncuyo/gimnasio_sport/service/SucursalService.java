package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DireccionDto;
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

    @Transactional
    public Sucursal crearSucursal(String nombre, Long empresaId, DireccionDto direccionDto) {
        if (sucursalRepository.existsByNombreAndEliminadoFalse(nombre)) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }
        Direccion direccion = direccionService.crearDireccion(direccionDto);

        Sucursal sucursal = Sucursal.builder()
                .nombre(nombre)
                .empresa(empresaRepository.findById(empresaId).orElseThrow(() -> new BusinessException("Empresa no encontrada")))
                .direccion(direccion)
                .eliminado(false)
                .build();
        return sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminarSucursal(Long id){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
        sucursal.setEliminado(true);
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void modificarSucursal(Long id, String nombre, Empresa empresa, DireccionDto direccionDto){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));

        if (sucursalRepository.existsByNombreAndIdNotAndEliminadoFalse(nombre, id))
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        Direccion direccion = direccionService.crearDireccion(direccionDto);

        sucursal.setNombre(nombre);
        sucursal.setEmpresa(empresa);
        sucursal.setDireccion(direccion);
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public Sucursal buscarSucursal(Long id) {
        return sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
    }

    @Transactional
    public List<SucursalResumenDTO> listarSucursalResumenDto() {
        List<Sucursal> sucursales = sucursalRepository.findAllByEliminadoFalse();
        return sucursalMapper.toSummaryDtos(sucursales);
    }
}
