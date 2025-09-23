package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.ValorCuotaDto;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.ValorCuotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ValorCuotaController {
    private final String listView = "valorCuota/list";
    private final String createView = "valorCuota/alta";
    private final String redirect = "/valoresCuota";
    private final ValorCuotaService valorCuotaService;

    @GetMapping("/valoresCuota")
    public String listarValoresCuota(Model model) {
        try {
            return prepararVistaListaValoresCuota(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

    @GetMapping("/valoresCuota/alta")
    public String altaValorCuota(Model model) {
        try {
            return prepararVistaFormularioAlta(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaValoresCuota(model);
    }

    @PostMapping("/valoresCuota/alta")
    public String altaValorCuota(Model model, @Valid @ModelAttribute("valorCuota") ValorCuotaDto valorCuota, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, valorCuota);

        try {
            valorCuotaService.crearValorCuota(valorCuota);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioAlta(model, valorCuota);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioAlta(model, valorCuota);
        }
    }

    @PostMapping("/valoresCuota/{id}/baja")
    public String eliminarValorCuota(Model model, @PathVariable Long id) {
        try {
            valorCuotaService.eliminarValorCuota(id);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaValoresCuota(model);
    }

    private String prepararVistaListaValoresCuota(Model model) {
        List<ValorCuotaDto> valoresCuota = valorCuotaService.listarValoresCuotaDtosActivos();
        model.addAttribute("valoresCuota", valoresCuota);
        return listView;
    }

    private String prepararVistaFormularioAlta(Model model) {
        return prepararVistaFormularioAlta(model, new ValorCuotaDto());
    }

    private String prepararVistaFormularioAlta(Model model, ValorCuotaDto valorCuota) {
        model.addAttribute("valorCuota", valorCuota);
        return createView;
    }
}
