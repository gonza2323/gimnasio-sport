package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rutinas")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    public ResponseEntity<RutinaDto> crear(@RequestBody RutinaDto dto) {
        RutinaDto creada = rutinaService.crear(dto);
        return ResponseEntity.created(URI.create("/rutinas/" + creada.getId()))
                .body(creada);
    }

    @GetMapping
    public ResponseEntity<List<RutinaDto>> listar() {
        return ResponseEntity.ok(rutinaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rutinaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaDto> actualizar(@PathVariable Long id, @RequestBody RutinaDto dto) {
        return ResponseEntity.ok(rutinaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

