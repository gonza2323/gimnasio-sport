package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.dto.PagoCuotasDto;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.CuotaMensualService;
import ar.edu.uncuyo.gimnasio_sport.service.SucursalService;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
    public String pagarCuotas(Model model, @ModelAttribute PagoCuotasDto dto) throws Exception {
        List<CuotaMensualDto> cuotas = cuotaMensualService.buscarCuotasParaPagoDeSocioActual(dto.getCuotasSeleccionadas());

        List<PreferenceItemRequest> items = cuotas.stream().map(cuota -> {
            YearMonth ym = YearMonth.of(cuota.getAnio().intValue(), cuota.getMes());
            DateTimeFormatter fmtShort = DateTimeFormatter.ofPattern("MM/yy");
            String fechaFormateada = ym.format(fmtShort);

            return PreferenceItemRequest.builder()
                    .id(cuota.getId().toString())
                    .title("Cuota " + fechaFormateada + " Gimnasio Sport")
                    .description("Cuota de Gimnasio Sport")
                    .categoryId("services")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(new BigDecimal(cuota.getMonto()))
                    .build();
        }).toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://gym.gpadilla.com/success")
                .pending("https://gym.gpadilla.com/pending")
                .failure("https://gym.gpadilla.com/failure")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);
        return "redirect:" + preference.getInitPoint();
    }

    @GetMapping("/success")
    public String success(@RequestParam Map<String, String> params, Model model,
                          RedirectAttributes redirectAttributes) throws Exception {
        String paymentId = params.get("payment_id");

        PaymentClient client = new PaymentClient();
        Payment payment = client.get(Long.parseLong(paymentId));

        if (!payment.getStatus().equals("approved")) {
            redirectAttributes.addFlashAttribute("msgError", "ERROR EN EL PAGO");
        }

        List<PaymentItem> items = payment.getAdditionalInfo().getItems();

        redirectAttributes.addFlashAttribute("msgExito", "PAGO EXITOSO");
        return "redirect:" + redirect;
    }

    @GetMapping("/failure")
    public String failure(@RequestParam Map<String, String> params, Model model,
                          RedirectAttributes redirectAttributes) throws Exception {
        redirectAttributes.addFlashAttribute("msgError", "ERROR EN EL PAGO");
        return "redirect:" + redirect;
    }

    private String prepararVistaListaCuotasPropiasSocio(Model model) {
        double deudaTotal = cuotaMensualService.getDeudaTotalSocioActual();
        List<CuotaMensualDto> cuotas = cuotaMensualService.listarCuotasMensualesDtosPropias();
        model.addAttribute("deudaTotal", deudaTotal);
        model.addAttribute("cuotas", cuotas);
        return listView;
    }
}
