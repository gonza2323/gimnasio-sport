package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rutinas")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @PostMapping
    public ResponseEntity<RutinaDto> crear(@RequestBody RutinaDto dto, UriComponentsBuilder uriBuilder) {
        RutinaDto creada = rutinaService.crear(dto);
        if (creada.getId() == null) {
            throw new IllegalStateException("Rutina creada sin identificador");
        }
        URI location = uriBuilder.path("/rutinas/{id}")
                .buildAndExpand(creada.getId())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<RutinaDto>> listar() {
        return ResponseEntity.ok(rutinaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaDto> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(rutinaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaDto> actualizar(@PathVariable("id") Long id,
                                                @RequestBody RutinaDto dto) {
        return ResponseEntity.ok(rutinaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
