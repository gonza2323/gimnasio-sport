package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.PaisDto;
import ar.edu.uncuyo.gimnasio_sport.dto.SucursalResumenDTO;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.PaisService;
import ar.edu.uncuyo.gimnasio_sport.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SucursalController {
    private final SucursalService sucursalService;
    private final String listView = "sucursal/list";
    private final String createView = "sucursal/alta";
    private final PaisService paisService;

    @GetMapping("/sucursales")
    public String listarSucursales(Model model) {
        try {
            List<SucursalResumenDTO> sucursales = sucursalService.listarSucursalResumenDto();
            model.addAttribute("sucursales", sucursales);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

    @GetMapping("/sucursales/alta")
    public String altaSucursal(Model model) {
        try {
            List<PaisDto> paises = paisService.listarPaisesDtos();
            model.addAttribute("paises", paises);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return createView;
    }
}
