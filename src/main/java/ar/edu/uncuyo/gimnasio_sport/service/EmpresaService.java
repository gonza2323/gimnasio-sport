package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DireccionDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Direccion;
import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.entity.Sucursal;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    @Transactional
    public Empresa crearEmpresa(String nombre, String telefono, String mail) {
        if (empresaRepository.existsByNombreAndEliminadoFalse(nombre)) {
            throw new BusinessException("Ya existe una empresa con ese nombre");
        }
        Empresa empresa = Empresa.builder()
                .nombre(nombre)
                .telefono(telefono)
                .mail(mail)
                .eliminado(false)
                .build();
        return empresaRepository.save(empresa);

    }

    @Transactional
    public void eliminarEmpresa(Long id){
        Empresa empresa = empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
        empresa.setEliminado(true);
        empresaRepository.save(empresa);
    }

    @Transactional
    public void modificarEmpresa(Long id, String nombre, String telefono, String mail){
        Empresa empresa = empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));

        if (empresaRepository.existsByNombreAndIdNotAndEliminadoFalse(nombre, id))
            throw new BusinessException("Ya existe una empresa con ese nombre");

        empresa.setNombre(nombre);
        empresa.setTelefono(telefono);
        empresa.setMail(mail);
        empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa buscarEmpresa(Long id) {
        return empresaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Empresa no encontrada"));
    }
}
