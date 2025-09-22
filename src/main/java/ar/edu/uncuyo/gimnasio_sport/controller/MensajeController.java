package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.FiltroMensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.dto.PromocionDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.entity.Promocion;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import ar.edu.uncuyo.gimnasio_sport.service.PromotionSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;
    private final UsuarioRepository usuarioRepository;
    private final PromotionSchedulerService promotionSchedulerService;

    // View / redirect constants
    private static final String VIEW_LIST = "mensajes/list";
    private static final String VIEW_PROGRAMAR = "mensajes/programar";
    private static final String VIEW_FORM_MENSAJE = "mensajes/formMensaje";
    private static final String VIEW_PROMOCIONES_LIST = "mensajes/promociones-list";
    private static final String VIEW_PROMOCIONES_PROGRAMAR = "mensajes/promociones-programar";

    private static final String REDIRECT_MENSAJES = "redirect:/mensajes/enviar";
    private static final String REDIRECT_PROMOCION = "redirect:/mensajes/promocion";

    @GetMapping
    public String listar(@ModelAttribute("f") FiltroMensajeDTO filtro,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model) {
        if (filtro == null) {
            filtro = new FiltroMensajeDTO();
        }
        Page<Mensaje> pagina = mensajeService.listar(filtro, PageRequest.of(page, size));
        Page<MensajeDTO> paginaDto = pagina.map(mensajeService::toDto);
        model.addAttribute("mensajes", paginaDto.getContent());
        model.addAttribute("page", paginaDto);
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
    public String guardarProgramacion(@Valid @ModelAttribute("mensajeDto") MensajeDTO dto,
                                      BindingResult result,
                                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            model.addAttribute("readOnly", false);
            return VIEW_PROGRAMAR;
        }
        Usuario usuario = usuarioActual();
        dto.setUsuarioId(usuario.getId());
        if (dto.getId() == null) {
            mensajeService.crear(dto);
        } else {
            mensajeService.actualizar(dto.getId(), dto);
        }
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

    @GetMapping("/enviar")
    public String enviarForm(Model model) {
        if (!model.containsAttribute("mensajeDto")) {
            MensajeDTO dto = nuevoMensaje();
            dto.setFechaProgramada(null);
            model.addAttribute("mensajeDto", dto);
        }
        model.addAttribute("tipos", TipoMensaje.values());
        model.addAttribute("readOnly", false);
        return VIEW_FORM_MENSAJE;
    }

    @PostMapping("/enviar")
    public String enviar(@Valid @ModelAttribute("mensajeDto") MensajeDTO dto,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("tipos", TipoMensaje.values());
            model.addAttribute("readOnly", false);
            return VIEW_FORM_MENSAJE;
        }
        Usuario usuario = usuarioActual();
        dto.setUsuarioId(usuario.getId());
        if (dto.getFechaProgramada() == null) {
            dto.setFechaProgramada(LocalDateTime.now());
        }
        mensajeService.crear(dto);
        return REDIRECT_MENSAJES;
    }

    @GetMapping("/promocion")
    public String promociones(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        FiltroMensajeDTO filtro = new FiltroMensajeDTO();
        filtro.setTipoMensaje(TipoMensaje.PROMOCION);
        Page<Mensaje> pagina = mensajeService.listar(filtro, PageRequest.of(page, size));
        List<PromocionDTO> data = pagina.getContent().stream()
                .map(this::toPromocionDto)
                .collect(Collectors.toList());
        Page<PromocionDTO> paginaDto = new PageImpl<>(data, pagina.getPageable(), pagina.getTotalElements());
        model.addAttribute("promociones", paginaDto.getContent());
        model.addAttribute("page", paginaDto);
        return VIEW_PROMOCIONES_LIST;
    }

    @GetMapping("/promocion/programar")
    public String programarPromocion(Model model) {
        if (!model.containsAttribute("promocionDto")) {
            PromocionDTO dto = new PromocionDTO();
            dto.setFechaProgramada(LocalDateTime.now().plusHours(1));
            model.addAttribute("promocionDto", dto);
        }
        return VIEW_PROMOCIONES_PROGRAMAR;
    }

    @PostMapping("/promocion/programar")
    public String guardarPromocion(@Valid @ModelAttribute("promocionDto") PromocionDTO dto,
                                   BindingResult result) {
        if (result.hasErrors()) {
            return VIEW_PROMOCIONES_PROGRAMAR;
        }
        promotionSchedulerService.programarPromocion(dto.getAsunto(), dto.getCuerpo(), dto.getFechaProgramada().atZone(ZoneId.systemDefault()));
        return REDIRECT_PROMOCION;
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

    private PromocionDTO toPromocionDto(Mensaje mensaje) {
        PromocionDTO dto = new PromocionDTO();
        dto.setId(mensaje.getId());
        dto.setAsunto(mensaje.getAsunto());
        dto.setCuerpo(mensaje.getCuerpo());
        dto.setFechaProgramada(mensaje.getFechaProgramada());
        if (mensaje instanceof Promocion promocion) {
            dto.setFechaEnvioPromocion(promocion.getFechaEnvioPromocion());
            dto.setCantidadSociosEnviados(promocion.getCantidadSociosEnviados());
        }
        return dto;
    }
}
