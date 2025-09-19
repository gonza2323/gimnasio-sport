package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SucursalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SucursalService {
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;

    @Transactional
    public void crearSucursal(String nombre, Long empresaId, Direccion direccion) {
        if (sucursalRepository.existsByNombreAndEliminadoFalse(nombre)) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }

        Sucursal sucursal = Sucursal.builder()
                .nombre(nombre)
                .empresa(empresaRepository.findById(empresaId).orElseThrow(() -> new BusinessException("Empresa no encontrada")))
                .direccion(direccion)
                .eliminado(false)
                .build();
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void eliminarSucursal(Long id){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
        sucursal.setEliminado(true);
        sucursalRepository.save(sucursal);
    }

    @Transactional
    public void modificarSucursal(Long id, String nombre, Empresa empresa, Direccion direccion){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));

        if (sucursalRepository.existsByNombreAndIdNotAndEliminadoFalse(nombre, id))
            throw new BusinessException("Ya existe una sucursal con ese nombre");

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
}
