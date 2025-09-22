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
public class SocioController {
    private final SocioService socioService;
    private final PaisService paisService;

    private final String listView = "socio/list";
    private final String detailView = "socio/detalle";
    private final String createView = "socio/alta";
    private final String editView = "socio/edit";
    private final String redirect = "/socios";
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;
    private final SucursalService sucursalService;

    @GetMapping("/socios")
    public String listarSocios(Model model) {
        try {
            return prepararVistaListaSocios(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

//    @GetMapping("/socios/{id}")
//    public String detalleSocio(Model model, @PathVariable Long id) {
//        try {
//            SocioDto socio = socioService.buscarSocioDto(id);
//            return prepararVistaDetalle(model, socio);
//        } catch (BusinessException e) {
//            model.addAttribute("msgError", e.getMessageKey());
//        } catch (Exception e) {
//            model.addAttribute("msgError", "error.sistema");
//        }
//        return prepararVistaListaSocios(model);
//    }

    @GetMapping("/socios/alta")
    public String altaSocio(Model model) {
        try {
            return prepararVistaFormularioAlta(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSocios(model);
    }

    @PostMapping("/socios/alta")
    public String altaSocio(Model model, @Valid @ModelAttribute("socio") SocioCreateFormDto socio, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, socio);

        try {
            socioService.crearSocio(socio);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioAlta(model, socio);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioAlta(model, socio);
        }
    }

    @PostMapping("/socios/{id}/baja")
    public String eliminarSocio(Model model, @PathVariable Long id) {
        try {
            socioService.eliminarSocio(id);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaSocios(model);
    }

    private void poblarCamposDireccion(Model model, DireccionDto direccion) {
        List<PaisDto> paises = paisService.listarPaisesDtos();
        model.addAttribute("paises", paises);

        Long paisId = direccion.getPaisId();
        if (paisId != null) {
            List<ProvinciaDto> provincias = provinciaService.buscarProvinciaPorPais(paisId);
            model.addAttribute("provincias", provincias);
        }

        Long provinciaId = direccion.getProvinciaId();
        if (provinciaId != null) {
            List<DepartamentoDto> departamentos = departamentoService.buscarDepartamentosPorProvincia(provinciaId);
            model.addAttribute("departamentos", departamentos);
        }

        Long departamentoId = direccion.getDepartamentoId();
        if (departamentoId != null) {
            List<LocalidadDto> localidades = localidadService.buscarLocalidadesPorDepartamento(departamentoId);
            model.addAttribute("localidades", localidades);
        }
    }

    private void prepararVistaFormulario(Model model, DireccionDto direccion) {
        List<SucursalResumenDTO> sucursales = sucursalService.listarSucursalResumenDto();
        model.addAttribute("sucursales", sucursales);

        poblarCamposDireccion(model, direccion);
    }

    private String prepararVistaFormularioAlta(Model model) {
        model.addAttribute("socio", new SocioCreateFormDto());
        return createView;
    }

    private String prepararVistaFormularioAlta(Model model, SocioCreateFormDto socio) {
        prepararVistaFormulario(model, socio.getDireccion());
        return createView;
    }

    private String prepararVistaListaSocios(Model model) {
        List<SocioResumenDto> socios = socioService.listarSocioResumenDtos();
        model.addAttribute("socios", socios);
        return listView;
    }
}
