package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;

import java.util.List;

public interface RutinaService {

    RutinaDto crear(RutinaDto dto);

    List<RutinaDto> listar();

    RutinaDto buscarPorId(Long id);

    RutinaDto actualizar(Long id, RutinaDto dto);

    void eliminar(Long id);
}

