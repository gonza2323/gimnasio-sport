package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.CuotaMensualDto;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoDePago;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentItem;
import com.mercadopago.resources.preference.Preference;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@EnableAsync
public class MercadoPagoService {
    private final CuotaMensualService cuotaMensualService;
    private final PaymentClient paymentClient;

    public MercadoPagoService(CuotaMensualService cuotaMensualService) {
        String tokenMercadoPago = System.getenv("MP_TOKEN");
        MercadoPagoConfig.setAccessToken(tokenMercadoPago);

        this.cuotaMensualService = cuotaMensualService;
        this.paymentClient = new PaymentClient();
    }

    @Transactional
    public String generarLinkDePagoCuotasSeleccionadas(List<Long> cuotasId) throws Exception {
        List<CuotaMensualDto> cuotas = cuotaMensualService.buscarCuotasParaPagoDeSocioActual(cuotasId);

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

        // No permitimos pagar en efectivo, así no queda pendiente el pago
        PreferencePaymentTypeRequest excludedType = PreferencePaymentTypeRequest.builder()
                .id("ticket").build();

        PreferencePaymentMethodsRequest paymentMethods =
                PreferencePaymentMethodsRequest.builder()
                        .excludedPaymentTypes(Arrays.asList(excludedType))
                        .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .paymentMethods(paymentMethods)
                .backUrls(backUrls)
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

    @Async
    @Transactional
    public void procesarPago(Long id) throws Exception {
        System.out.println("Procesando pago con ID: " + id + "...");
        Payment payment = paymentClient.get(id);

        if (!payment.getStatus().equals("approved"))
            return;

        List<PaymentItem> items = payment.getAdditionalInfo().getItems();
        List<Long> cuotasIds = items.stream().map(item -> Long.parseLong(item.getId())).toList();

        cuotaMensualService.pagarCuotas(cuotasIds, TipoDePago.BILLETERA_VIRTUAL);
    }
}
