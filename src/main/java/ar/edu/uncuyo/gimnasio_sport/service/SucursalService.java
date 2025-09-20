package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SucursalService {
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;

    public void crearSucursal(String nombre, Long empresaId, Direccion direccion) {
        if (sucursalRepository.existsByNombreAndEliminadoFalse(nombre)) {
            throw new BusinessException("Ya existe una sucursal con ese nombre");
        }
        Sucursal sucursal = Sucursal.builder()
                .nombre(nombre)
                .empresa(empresaRepository.findById(empresaId).orElseThrow(() -> new BusinessException("Empresa no encontrada")))
                .direccion(direccion) //crear direccion antes?
                .eliminado(false)
                .build();
        sucursalRepository.save(sucursal);
    }



    public void eliminarSucursal(Long id){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
        sucursal.setEliminado(true);
        sucursalRepository.save(sucursal);
    }

    public void modificarSucursal(Long id, String nombre, Empresa empresa, Direccion direccion){
        Sucursal sucursal = sucursalRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
        sucursal.setNombre(nombre);
        sucursal.setEmpresa(empresa);
        sucursal.setDireccion(direccion);
        sucursalRepository.save(sucursal);
    }

}
