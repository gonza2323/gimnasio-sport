package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.model.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.model.Usuario;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import ar.edu.uncuyo.gimnasio_sport.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mensajes")
@RequiredArgsConstructor //no hace falta el @Autowired ya que esto hace constructor injection
public class MensajeController {


    private final MensajeService mensajeService;
    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(@ModelAttribute("f") FiltroMensajeDTO f,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model) {
        Page<Mensaje> mensajes = mensajeService.listar(f, PageRequest.of(page, size));
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("tipos", TipoMensaje.values());
        return "view/mensaje/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("mensaje", new MensajeDTO());
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("usuarios", listarUsuarios());
        return "view/mensaje/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("mensaje") MensajeDTO dto, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            model.addAttribute("usuarios", listarUsuarios());
            return "view/mensaje/form";
        }
        mensajeService.crear(dto);
        return "redirect:/mensajes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Mensaje m = mensajeService.obtener(id);
        MensajeDTO dto = new MensajeDTO();
        dto.setId(m.getId());
        dto.setTitulo(m.getTitulo());
        dto.setTexto(m.getTexto());
        dto.setTipoMensaje(m.getTipoMensaje());
        dto.setUsuarioId(m.getUsuario().getId());
        model.addAttribute("mensaje", dto);
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("usuarios", listarUsuarios());
        return "view/mensaje/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("mensaje") MensajeDTO dto,
                             BindingResult binding,
                             Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            model.addAttribute("usuarios", listarUsuarios());
            return "view/mensaje/form";
        }
        mensajeService.actualizar(id, dto);
        return "redirect:/mensajes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, jakarta.servlet.http.HttpSession session) {
        ar.edu.uncuyo.gimnasio_sport.model.Usuario actual = (ar.edu.uncuyo.gimnasio_sport.model.Usuario) session.getAttribute(LoginController.SESSION_USER);
        if (actual == null) {
            throw new IllegalArgumentException("No autenticado");
        }
        // Ambos roles pueden eliminar mensajes en este demo
        mensajeService.eliminarLogico(id);
        return "redirect:/mensajes";
    }

    private List<Usuario> listarUsuarios() {
        return usuarioService.listar(null, PageRequest.of(0, 1000)).getContent();
    }
}
