package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.error.FieldSpecificBusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.PaisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/paises")
@RequiredArgsConstructor
public class PaisController {
    private final PaisService paisService;

    private final String listView = "/view/pais/lista";

    @GetMapping("/{id}")
    public String detallePais(Model model, @PathVariable Long id) {
        try {
            PaisDto pais = paisService.buscarPaisDto(id);
            model.addAttribute("pais", pais);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return listView;
        }
        return "view/pais/detalle";
    }

    @GetMapping
    public String listarPaises(Model model) {
        try {
            List<PaisDto> paises = paisService.listarPaisesDto();
            model.addAttribute("paises", paises);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return listView;
        }
        return listView;
    }

    @GetMapping("/{id}/editar")
    public String editPais(Model model, @PathVariable Long id) {
        try {
            PaisDto pais = paisService.buscarPaisDto(id);
            model.addAttribute("pais", pais);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return listView;
        }
        return "view/pais/editar";
    }

    @GetMapping("/alta")
    public String altaPais(Model model) {
        model.addAttribute("pais", new PaisDto());
        return "view/pais/alta";
    }

    @PostMapping("/aceptarEdit")
    public String aceptarEdit(@Valid @ModelAttribute("pais") PaisDto pais, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "view/pais/editar";
        }

        try {
            paisService.modificarPais(pais);
        } catch (FieldSpecificBusinessException e) {
            bindingResult.rejectValue(e.getField(), e.getMessageKey());
            return "view/pais/editar";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return "view/pais/editar";
        }

        return "redirect:/paises";
    }

    @PostMapping("/aceptarCrear")
    public String aceptarCrear(@Valid @ModelAttribute("pais") PaisDto pais, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "view/pais/alta";
        }

        try {
            paisService.crearPais(pais);
        } catch (FieldSpecificBusinessException e) {
            bindingResult.rejectValue(e.getField(), e.getMessageKey());
            return "view/pais/alta";
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return "view/pais/alta";
        }

        return "redirect:/paises";
    }

    @GetMapping("/{id}/baja")
    public String bajaPais(Model model, @PathVariable Long id) {
        try {
            paisService.eliminarPais(id);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return listView;
        }

        return "redirect:/paises";
    }
}
