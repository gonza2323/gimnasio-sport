package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.*;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoDocumento;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
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
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PaisService paisService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;
    private final SucursalService sucursalService;

    private final String listView = "empleado/list";
    private final String detailView = "empleado/detalle";
    private final String createView = "empleado/alta";
    private final String editView = "empleado/edit";
    private final String redirect = "/empleados";

    @GetMapping("/empleados")
    public String listarEmpleados(Model model) {
        try {
            return prepararVistaListaEmpleados(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return listView;
    }

    @GetMapping("/empleados/alta")
    public String altaEmpleado(Model model) {

        try {
            return prepararVistaFormularioAlta(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaEmpleados(model);
    }

    @PostMapping("/empleados/alta")
    public String altaEmpleado(Model model,
                               @Valid @ModelAttribute("empleado") EmpleadoCreateForm empleado,
                               BindingResult bindingResult) {
        System.out.println("Empleado recibido: " + empleado);
        System.out.println("Errores de binding: " + bindingResult.getAllErrors());
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, empleado);

        try {
            empleadoService.crearEmpleado(empleado);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioAlta(model, empleado);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioAlta(model, empleado);
        }
    }

    @PostMapping("/empleados/{id}/baja")
    public String eliminarEmpleado(Model model, @PathVariable Long id) {
        try {
            empleadoService.eliminarEmpleado(id);
            return "redirect:" + redirect;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return prepararVistaListaEmpleados(model);
    }

    // --- Métodos de apoyo ---
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
        EmpleadoCreateForm form = new EmpleadoCreateForm();
        form.setPersona(new PersonaCreateFormDTO());
        form.getPersona().setDireccion(new DireccionDto());
        form.getPersona().setUsuario(new UsuarioCreateFormDTO());

        model.addAttribute("empleado", form);

        // cargar enums y combos
        model.addAttribute("tiposEmpleado", TipoEmpleado.values());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("sucursales", sucursalService.listarSucursalResumenDto());

        poblarCamposDireccion(model, form.getPersona().getDireccion());

        return createView;
    }

    private String prepararVistaFormularioAlta(Model model, EmpleadoCreateForm empleado) {
        if (empleado.getPersona() == null) {
            empleado.setPersona(new PersonaCreateFormDTO());
        }
        if (empleado.getPersona().getDireccion() == null) {
            empleado.getPersona().setDireccion(new DireccionDto());
        }
        if (empleado.getPersona().getUsuario() == null) {
            empleado.getPersona().setUsuario(new UsuarioCreateFormDTO());
        }

        prepararVistaFormulario(model, empleado.getPersona().getDireccion());

        // cargar enums y combos
        model.addAttribute("tiposEmpleado", TipoEmpleado.values());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
        model.addAttribute("sucursales", sucursalService.listarSucursalResumenDto());

        return createView;
    }

    private String prepararVistaListaEmpleados(Model model) {
        List<EmpleadoResumenDto> empleados = empleadoService.listarEmpleadoResumenDtos();
        model.addAttribute("empleados", empleados);
        return listView;
    }
}
