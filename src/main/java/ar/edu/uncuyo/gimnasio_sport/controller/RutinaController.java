package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleRutinaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.PersonaService;
import ar.edu.uncuyo.gimnasio_sport.service.RutinaService;
import ar.edu.uncuyo.gimnasio_sport.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;
    private final PersonaService personaService;
    private final SocioRepository socioRepository;

    private final String listView = "rutina/list";
    private final String editView = "rutina/edit";
    private final String createView = "rutina/alta";
    private final String redirectProfesor = "/tablero-rutinas";
    private final SocioService socioService;

    @GetMapping("/tablero-rutinas")
    public String tableroProfesor(Model model) {

        model.addAttribute("estadosRutina", EstadoRutina.values());
        model.addAttribute("rutinas", new ArrayList<RutinaDto>());
        model.addAttribute("rutinasPorSocio", new LinkedHashMap<Long, List<RutinaDto>>());
        model.addAttribute("resumenSocios", new LinkedHashMap<Long, RutinaDto>());
        model.addAttribute("socios", new LinkedHashMap<Long, String>());

        try {
            List<RutinaDto> rutinas = rutinaService.listarPorProfesorActual();
            Map<Long, List<RutinaDto>> rutinasPorSocio = agruparPorSocio(rutinas);
            Map<Long, RutinaDto> resumen = crearResumenSocios(rutinasPorSocio);

            model.addAttribute("rutinas", rutinas);
            model.addAttribute("rutinasPorSocio", rutinasPorSocio);
            model.addAttribute("resumenSocios", resumen);


            Map<Long, String> socios = new LinkedHashMap<>();
            for (Socio s : socioRepository.findAllByEliminadoFalse()) {
                String nombre = (s.getNombre() != null ? s.getNombre() : "") + " " +
                        (s.getApellido() != null ? s.getApellido() : "");
                String correo = s.getCorreoElectronico();
                socios.put(s.getId(), correo != null ? nombre + " (" + correo + ")" : nombre);
            }
            model.addAttribute("socios", socios);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }

        return listView;
    }

    @GetMapping("/rutinas/alta")
    public String altaRutina(Model model) {
        try {
            return prepararVistaFormularioAlta(model);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return "";
    }

    @GetMapping("/rutinas/{id}/edit")
    public String modificarRutina(Model model, @PathVariable Long id) {
        try {
            RutinaDto rutina = rutinaService.buscarRutinaDto(id);
            return prepararVistaFormularioEdicion(model, rutina);
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return "";
    }

    @PostMapping("/rutinas/alta")
    public String altaRutina(Model model, @Valid @ModelAttribute("rutinaForm") RutinaDto rutina,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioAlta(model, rutina);

        try {
            rutinaService.crear(rutina);
            redirectAttributes.addFlashAttribute("msgExito", "Rutina creada correctamente");
            return "redirect:" + redirectProfesor;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioAlta(model, rutina);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioAlta(model, rutina);
        }
    }

    @PostMapping("/rutinas/edit")
    public String modificarRutina(Model model, @Valid @ModelAttribute("rutinaForm") RutinaDto rutina,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors())
            return prepararVistaFormularioEdicion(model, rutina);

        try {
            rutinaService.actualizar(rutina);
            redirectAttributes.addFlashAttribute("msgExito", "Rutina creada correctamente");
            return "redirect:" + redirectProfesor;
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return prepararVistaFormularioEdicion(model, rutina);
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return prepararVistaFormularioEdicion(model, rutina);
        }
    }

    private void prepararVistaFormulario(Model model) {
        model.addAttribute("socios", socioService.listarSocioResumenDtos());
    }

    private String prepararVistaFormularioAlta(Model model) {
        RutinaDto rutina = RutinaDto.builder()
                .fechaInicio(LocalDate.now())
                .fechaFinalizacion(LocalDate.now().plusDays(7))
                .detalles(List.of(
                        DetalleRutinaDto.builder()
                                .fecha(LocalDate.now())
                                .actividad("PIERNAS")
                                .build()
                ))
                .build();

        model.addAttribute("rutinaForm", rutina);
        prepararVistaFormulario(model);
        return createView;
    }

    private String prepararVistaFormularioAlta(Model model, RutinaDto rutinaDto) {
        model.addAttribute("rutinaForm", rutinaDto);
        prepararVistaFormulario(model);
        return createView;
    }

    private String prepararVistaFormularioEdicion(Model model, RutinaDto rutinaDto) {
        model.addAttribute("rutinaForm", rutinaDto);
        prepararVistaFormulario(model);
        return editView;
    }

    @GetMapping("/socio/{socioId}/rutinas")
    public String verRutinasPorSocio(@PathVariable Long socioId, Model model) {
        try {
            Socio socio = socioRepository.findByIdAndEliminadoFalse(socioId)
                    .orElseThrow(() -> new BusinessException("rutina.socio.noEncontrado"));
            model.addAttribute("socioNombre", (socio.getNombre() + " " + socio.getApellido()).trim());
            model.addAttribute("rutinas", rutinaService.listarPorSocio(socioId));
        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }
        return "socio/listaRutina";
    }

    @GetMapping("/profesor/{profesorId}/{rutinaId}")
    public String verRutinaDeSocio(@PathVariable Long profesorId,
                                   @PathVariable Long rutinaId,
                                   Model model) {
        try {
            Empleado profesor = profesorActual();
            if (profesor == null || profesor.getTipoEmpleado() != TipoEmpleado.PROFESOR) {
                throw new BusinessException("acceso.denegado");
            }

            return tableroProfesor(model);

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return "list2";
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return "list2";
        }
    }

    @PostMapping("/rutinas/{rutinaId}/baja")
    public String eliminarRutina(@PathVariable Long rutinaId,
                                 RedirectAttributes redirectAttributes) {
        try {
            rutinaService.eliminar(rutinaId);
            redirectAttributes.addFlashAttribute("msgExito", "Rutina eliminada correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgError", "error.sistema");
        }
        return "redirect:" + redirectProfesor;
    }

    @GetMapping("/rutinas/{rutinaId}")
    public String verDetallesRutina(@PathVariable Long rutinaId,
                                    Model model) {
        try {
            RutinaDto rutina = rutinaService.buscarRutinaDto(rutinaId);

            model.addAttribute("rutina", rutina);
            return "rutina/detalle";

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return "rutina/detalle";
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return "rutina/detalle";
        }
    }


    // --- Métodos auxiliares ---

    private Empleado profesorActual() {
        try {
            Object persona = personaService.buscarPersonaActual();
            if (persona instanceof Empleado) {
                Empleado empleado = (Empleado) persona;
                if (empleado.getTipoEmpleado() == TipoEmpleado.PROFESOR) {
                    return empleado;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Socio socioActual() {
        try {
            Object persona = personaService.buscarPersonaActual();
            if (persona instanceof Socio) {
                Socio socio = (Socio) persona;
                if(!socio.isEliminado()) {
                    return socio;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Map<Long, List<RutinaDto>> agruparPorSocio(List<RutinaDto> rutinas) {
        Map<Long, List<RutinaDto>> resultado = new LinkedHashMap<>();
        for (RutinaDto rutina : rutinas) {
            Long socioId = rutina.getSocioId();
            if (socioId == null) continue;
            if (!resultado.containsKey(socioId)) {
                resultado.put(socioId, new ArrayList<RutinaDto>());
            }
            resultado.get(socioId).add(rutina);
        }
        return resultado;
    }

    private Map<Long, RutinaDto> crearResumenSocios(Map<Long, List<RutinaDto>> rutinasPorSocio) {
        Map<Long, RutinaDto> resumen = new LinkedHashMap<>();
        for (Map.Entry<Long, List<RutinaDto>> entry : rutinasPorSocio.entrySet()) {
            List<RutinaDto> datos = entry.getValue();
            if (datos != null && !datos.isEmpty()) {
                resumen.put(entry.getKey(), datos.get(0));
            }
        }
        return resumen;
    }

    private RutinaDto prepararRutinaForm(Long profesorId, Long rutinaId) {
        if (rutinaId == null) {
            return nuevaRutina(profesorId);
        }
        return rutinaService.buscarRutina(rutinaId);
    }

    private RutinaDto nuevaRutina(Long profesorId) {
        RutinaDto rutina = new RutinaDto();
        rutina.setProfesorId(profesorId);
        rutina.setFechaInicio(LocalDate.now());
        rutina.setFechaFinalizacion(LocalDate.now().plusDays(7));
        return rutina;
    }
}
