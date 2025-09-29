package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioRepository usuarioRepository;

    private static final String VIEW_LIST = "mensajes/list";
    private static final String VIEW_PROGRAMAR = "mensajes/programar";
    private static final String REDIRECT_MENSAJES = "redirect:/mensajes";

    @GetMapping()
    public String listar(@ModelAttribute("f") FiltroMensajeDTO filtro, Model model) {
        List<MensajeDTO> mensajes = mensajeService.listar(filtro).stream()
                .map(mensajeService::toDto)
                .collect(Collectors.toList());
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("tipos", TipoMensaje.values());
        return VIEW_LIST;
    }

    @GetMapping("/programar")
    public String programar(@RequestParam(value = "id", required = false) Long id,
                            @RequestParam(value = "readonly", defaultValue = "false") boolean readOnly,
                            Model model) {
        MensajeDTO dto = (id == null) ? nuevoMensaje() : mensajeService.toDto(mensajeService.obtener(id));
        if (dto.getFechaProgramada() == null) {
            dto.setFechaProgramada(LocalDateTime.now().plusHours(1));
        }
        model.addAttribute("mensajeDto", dto);
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("readOnly", readOnly);
        return VIEW_PROGRAMAR;
    }

    @PostMapping("/programar")
    public String guardarProgramacion(@ModelAttribute("mensajeDto") MensajeDTO dto, Model model) {
        Usuario usuario = usuarioActual();
        dto.setUsuarioId(usuario.getId());
        if (dto.getFechaProgramada() == null) {
            dto.setFechaProgramada(LocalDateTime.now());
        }
        if (dto.getId() == null) {
            mensajeService.crear(dto);
        } else {
            mensajeService.actualizar(dto.getId(), dto);
        }
        model.addAttribute("msgExito", "Mensaje guardado correctamente");
        return REDIRECT_MENSAJES;
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        MensajeDTO dto = mensajeService.toDto(mensajeService.obtener(id));
        model.addAttribute("mensajeDto", dto);
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("readOnly", true);
        return VIEW_PROGRAMAR;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        mensajeService.eliminarLogico(id);
        return REDIRECT_MENSAJES;
    }

    private MensajeDTO nuevoMensaje() {
        return new MensajeDTO();
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
    }
}
