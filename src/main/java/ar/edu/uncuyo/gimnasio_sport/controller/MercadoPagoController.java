package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.service.MercadoPagoService;
import com.mercadopago.net.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload,
                                                @RequestHeader Map<String, String> headers) {
        try {
            String type = (String) payload.get("type");
            Object id = payload.get("data") != null
                    ? ((Map<String, Object>) payload.get("data")).get("id")
                    : payload.get("id");

            if ("payment".equals(type) && id != null) {
                mercadoPagoService.procesarPago(Long.parseLong(id.toString()));
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }
}