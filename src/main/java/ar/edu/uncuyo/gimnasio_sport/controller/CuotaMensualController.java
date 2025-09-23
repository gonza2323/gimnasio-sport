package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.CuotaMensualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CuotaMensualController {
    private final CuotaMensualService cuotaMensualService;
    private final String redirectListaValoresCuota = "/valoresCuota";

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
}
