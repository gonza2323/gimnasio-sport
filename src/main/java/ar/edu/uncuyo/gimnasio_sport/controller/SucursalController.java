package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.*;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.service.*;
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
public class SucursalController {
    private final SucursalService sucursalService;
    private final PaisService paisService;

    private final String listView = "sucursal/list";
    private final String detailView = "sucursal/detalle";
    private final String createView = "sucursal/alta";
    private final String editView = "sucursal/edit";
    private final String redirect = "/sucursales";
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;

    @GetMapping("/sucursales")
    public String listarSucursales(Model model) {
        try {
            return prepararVistaListaSucursales(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

    @GetMapping("/sucursales/{id}")
    public String detalleSucursal(Model model, @PathVariable Long id) {
        try {
            SucursalDto sucursal = sucursalService.buscarSucursalDto(id);
            return prepararVistaDetalle(model, sucursal);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSucursales(model);
    }

    @GetMapping("/sucursales/alta")
    public String altaSucursal(Model model) {
        try {
            return prepararVistaFormularioAlta(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSucursales(model);
    }

    @GetMapping("/sucursales/{id}/edit")
    public String modificarSucursal(Model model, @PathVariable Long id) {
        try {
            SucursalDto sucursal = sucursalService.buscarSucursalDto(id);
            return prepararVistaFormularioEdicion(model, sucursal);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSucursales(model);
    }

    @PostMapping("/sucursales/alta")
    public String altaSucursal(Model model, @Valid @ModelAttribute("sucursal") SucursalDto sucursal, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, sucursal);

        try {
            sucursalService.crearSucursal(sucursal);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioAlta(model, sucursal);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioAlta(model, sucursal);
        }
    }

    @PostMapping("/sucursales/edit")
    public String modificarSucursal(Model model, @Valid @ModelAttribute("sucursal") SucursalDto sucursal, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, sucursal);

        try {
            sucursalService.modificarSucursal(sucursal);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioEdicion(model, sucursal);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioEdicion(model, sucursal);
        }
    }

    @PostMapping("/sucursales/{id}/baja")
    public String eliminarSucursal(Model model, @PathVariable Long id) {
        try {
            sucursalService.eliminarSucursal(id);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSucursales(model);
    }

    private void prepararVistaFormulario(Model model, SucursalDto sucursal) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = sucursal.getDireccion().getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        Long provinciaId = sucursal.getDireccion().getProvinciaId();
        if (provinciaId != null) {
            List<DepartamentoDto> departamentos = departamentoService.buscarDepartamentosPorProvincia(provinciaId);
            model.addAttribute("departamentos", departamentos);
        }

        Long departamentoId = sucursal.getDireccion().getDepartamentoId();
        if (departamentoId != null) {
            List<LocalidadDto> localidades = localidadService.buscarLocalidadesPorDepartamento(departamentoId);
            model.addAttribute("localidades", localidades);
        }

        model.addAttribute("sucursal", sucursal);
    }

    private String prepararVistaDetalle(Model model, SucursalDto sucursal) {
        prepararVistaFormulario(model, sucursal);
        return detailView;
    }

    private String prepararVistaFormularioAlta(Model model) {
        prepararVistaFormulario(model, new SucursalDto());
        return createView;
    }

    private String prepararVistaFormularioAlta(Model model, SucursalDto sucursal) {
        prepararVistaFormulario(model, sucursal);
        return createView;
    }

    private String prepararVistaFormularioEdicion(Model model, SucursalDto sucursal) {
        prepararVistaFormulario(model, sucursal);
        return editView;
    }

    private String prepararVistaListaSucursales(Model model) {
        List<SucursalResumenDTO> sucursales = sucursalService.listarSucursalResumenDto();
        model.addAttribute("sucursales", sucursales);
        return listView;
    }
}
