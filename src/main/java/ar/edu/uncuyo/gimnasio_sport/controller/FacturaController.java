package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.FacturaDto;
import ar.edu.uncuyo.gimnasio_sport.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FacturaController {

    private final FacturaService facturaService;

    @GetMapping("/facturas/{id}")
    public String detalleFactura(Model model, @PathVariable Long id) {
        FacturaDto factura = facturaService.buscarFacturaDto(id);
        model.addAttribute("factura", factura);
        return "factura/detalle";
    }

    @GetMapping("/facturas")
    public String listarFacturas(Model model) {
        List<FacturaDto> facturas = facturaService.listarFacturasActivas();
        model.addAttribute("facturas", facturas);
        return "factura/list";
    }

    @GetMapping("/me/facturas")
    public String listarFacturasSocioActual(Model model) {
        List<FacturaDto> facturas = facturaService.listarFacturasActivasSocioActual();
        model.addAttribute("facturas", facturas);
        return "factura/list";
    }
}
