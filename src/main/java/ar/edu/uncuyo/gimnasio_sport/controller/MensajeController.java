package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mensajes")
@RequiredArgsConstructor //no hace falta el @Autowired ya que esto hace constructor injection
public class MensajeController {


    private final MensajeService mensajeService;
    private final UsuarioRepository usuarioRepository;

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
        Usuario usuarioActual = obtenerUsuarioAutenticado();
        MensajeDTO mensaje = new MensajeDTO();
        mensaje.setUsuarioId(usuarioActual.getId());

        model.addAttribute("mensaje", mensaje);
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("usuarioActual", usuarioActual);
        return "view/mensaje/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("mensaje") MensajeDTO dto, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            return "view/mensaje/form";
        }
        Usuario usuarioActual = obtenerUsuarioAutenticado();
        dto.setUsuarioId(usuarioActual.getId());
        mensajeService.crear(dto);
        return "redirect:/mensajes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        Mensaje m = mensajeService.obtener(id);
        Usuario usuarioActual = obtenerUsuarioAutenticado();
        validarAccesoMensaje(m, usuarioActual);
        MensajeDTO dto = mensajeService.toDto(m);
        model.addAttribute("mensaje", dto);
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("usuarioActual", usuarioActual);
        return "view/mensaje/form";
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("mensaje") MensajeDTO dto,
                             BindingResult binding,
                             Model model) {
        if (binding.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            return "view/mensaje/form";
        }
        Usuario usuarioActual = obtenerUsuarioAutenticado();
        Mensaje mensaje = mensajeService.obtener(id);
        validarAccesoMensaje(mensaje, usuarioActual);
        Long usuarioDestinoId = mensaje.getUsuario() != null ? mensaje.getUsuario().getId() : usuarioActual.getId();
        dto.setUsuarioId(usuarioDestinoId);
        mensajeService.actualizar(id, dto);
        return "redirect:/mensajes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id) {
        Usuario usuarioActual = obtenerUsuarioAutenticado();
        Mensaje mensaje = mensajeService.obtener(id);
        validarAccesoMensaje(mensaje, usuarioActual);
        mensajeService.eliminarLogico(id);
        return "redirect:/mensajes";
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        String nombreUsuario = authentication.getName();
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(nombreUsuario)
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    private void validarAccesoMensaje(Mensaje mensaje, Usuario usuarioActual) {
        if (mensaje == null || usuarioActual == null) {
            throw new IllegalArgumentException("No se pudo validar el acceso al mensaje");
        }

        boolean esAdministrativo = usuarioActual.getRol() == RolUsuario.ADMINISTRATIVO;
        boolean esDuenoMensaje = mensaje.getUsuario() != null && mensaje.getUsuario().getId() != null
                && mensaje.getUsuario().getId().equals(usuarioActual.getId());

        if (!esAdministrativo && !esDuenoMensaje) {
            throw new IllegalArgumentException("No tiene permisos para operar sobre este mensaje");
        }
    }
}
