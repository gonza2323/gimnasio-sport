package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public void crearEmpresa(String nombre, String telefono, String mail) {
        if (empresaRepository.existsByNombreAndEliminadoFalse(nombre)) {
            throw new BusinessException("Ya existe una empresa con ese nombre");
        }
        Empresa empresa = Empresa.builder()
                .nombre(nombre)
                .telefono(telefono)
                .mail(mail)
                .eliminado(false)
                .build();
        empresaRepository.save(empresa);
    }
    
}
