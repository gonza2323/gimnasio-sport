package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.dto.PagoCuotasDto;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.CuotaMensualService;
import ar.edu.uncuyo.gimnasio_sport.service.FacturaService;
import ar.edu.uncuyo.gimnasio_sport.service.MercadoPagoService;
import ar.edu.uncuyo.gimnasio_sport.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CuotaMensualController {
    private final CuotaMensualService cuotaMensualService;
    private final String redirectListaValoresCuota = "/valoresCuota";
    private final String listView = "/cuotas/list";
    private final String redirect = "/me/cuotas";
    private final SucursalService sucursalService;
    private final MercadoPagoService mercadoPagoService;
    private final FacturaService facturaService;

    @PostMapping("cuotas/emitirCuotasMesActual")
    public String emitirCuotasMesActual(RedirectAttributes redirectAttributes) {
        try {
            long cantidadCuotasCreadas = cuotaMensualService.emitirCuotasMesActual();
            if (cantidadCuotasCreadas > 0) {
                redirectAttributes.addFlashAttribute("msgExito",
                        "Se emitieron " + cantidadCuotasCreadas + " cuotas para " + cantidadCuotasCreadas + " socios.");
            } else {
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
    public String pagarCuotasPorMercadoPago(Model model, @ModelAttribute PagoCuotasDto dto) throws Exception {
        String linkMercadoPago = mercadoPagoService.generarLinkDePagoCuotasSeleccionadas(dto.getCuotasSeleccionadas());
        return "redirect:" + linkMercadoPago;
    }

    @GetMapping("/success")
    public String success(@RequestParam Map<String, String> params, Model model,
                          RedirectAttributes redirectAttributes) throws Exception {
        String status = params.get("status");

        if (!status.equals("approved")) {
            redirectAttributes.addFlashAttribute("msgError", "error.pago");
        }

        redirectAttributes.addFlashAttribute("msgExito", "PAGO EXITOSO");
        return "redirect:" + redirect;
    }

    @GetMapping("/pending")
    public String pending(@RequestParam Map<String, String> params, Model model,
                          RedirectAttributes redirectAttributes) throws Exception {
        redirectAttributes.addFlashAttribute("msg", "SU PAGO ESTÁ PENDIENDE DE APROBACIÓN. NO INTENTE PAGAR DE NUEVO.");
        return "redirect:" + redirect;
    }

    @GetMapping("/failure")
    public String failure(@RequestParam Map<String, String> params, Model model,
                          RedirectAttributes redirectAttributes) throws Exception {
        redirectAttributes.addFlashAttribute("msgError", "error.pago");
        return "redirect:" + redirect;
    }

    private String prepararVistaListaCuotasPropiasSocio(Model model) {
        double deudaTotal = cuotaMensualService.getDeudaTotalSocioActual();
        List<CuotaMensualDto> cuotas = cuotaMensualService.listarCuotasMensualesDtosPropias();
        model.addAttribute("deudaTotal", deudaTotal);
        model.addAttribute("cuotas", cuotas);
        return listView;
    }

    @GetMapping("/cuotas/{id}/factura")
    public String buscarFacturaDeCuota(@PathVariable Long id) {
        Long idFactura = facturaService.buscarIdFacturaPorCuotaId(id);
        return "redirect:/facturas/" + idFactura.toString();
    }
}
