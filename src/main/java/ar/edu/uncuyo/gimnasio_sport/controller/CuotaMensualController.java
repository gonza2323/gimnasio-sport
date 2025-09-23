package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.dto.PagoCuotasDto;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.CuotaMensualService;
import ar.edu.uncuyo.gimnasio_sport.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CuotaMensualController {
    private final CuotaMensualService cuotaMensualService;
    private final String redirectListaValoresCuota = "/valoresCuota";
    private final String listView = "/cuotas/list";
    private final SucursalService sucursalService;

    @PostMapping("cuotas/emitirCuotasMesActual")
    public String emitirCuotasMesActual(RedirectAttributes redirectAttributes) {
        try {
            long cantidadCuotasCreadas = cuotaMensualService.emitirCuotasMesActual();
            if (cantidadCuotasCreadas > 0) {
                redirectAttributes.addFlashAttribute("msgExito",
                        "Se emitieron " + cantidadCuotasCreadas + " cuotas para " + cantidadCuotasCreadas + " socios.");
            }
            else {
                redirectAttributes.addFlashAttribute("msg", "No se emitieron cuotas. Todos los socios tienen cuota para el mes actual.");
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msgError", "error.sistema");
        }
        return "redirect:" + redirectListaValoresCuota;
    }

    @GetMapping("/me/cuotas")
    public String listarMisCuotasSocio(Model model) {
        try {
            return prepararVistaListaCuotasPropiasSocio(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

    @PostMapping("/cuotas/pagar")
    public String pagarCuotas(Model model, @ModelAttribute PagoCuotasDto dto) {
        System.out.println(dto.getCuotasSeleccionadas());
        return "redirect:/me/cuotas";
    }

    private String prepararVistaListaCuotasPropiasSocio(Model model) {
        double deudaTotal = cuotaMensualService.getDeudaTotalSocioActual();
        List<CuotaMensualDto> cuotas = cuotaMensualService.listarCuotasMensualesDtosPropias();
        model.addAttribute("deudaTotal", deudaTotal);
        model.addAttribute("cuotas", cuotas);
        return listView;
    }
}
