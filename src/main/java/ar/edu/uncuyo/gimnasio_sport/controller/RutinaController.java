package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.RutinaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Empleado;
import ar.edu.uncuyo.gimnasio_sport.entity.Socio;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoRutina;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.repository.SocioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.PersonaService;
import ar.edu.uncuyo.gimnasio_sport.service.RutinaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rutinas")
public class RutinaController {

    private final RutinaService rutinaService;
    private final PersonaService personaService;
    private final SocioRepository socioRepository;

    private final String listView = "rutina/list";
    private final String redirectProfesor = "/rutinas/profesor/";

    public RutinaController(RutinaService rutinaService,
                            PersonaService personaService,
                            SocioRepository socioRepository) {
        this.rutinaService = rutinaService;
        this.personaService = personaService;
        this.socioRepository = socioRepository;
    }

    @GetMapping
    public String inicioRutinas() {
        Empleado profesor = profesorActual();
        if (profesor == null) {
            return "redirect:/";
        }
        return "redirect:" + redirectProfesor + profesor.getId();
    }

    @GetMapping("/profesor/{profesorId}")
    public String tableroProfesor(@PathVariable Long profesorId,
                                  @RequestParam(value = "rutinaId", required = false) Long rutinaId,
                                  Model model) {

        model.addAttribute("profesorId", profesorId);
        model.addAttribute("estadosRutina", EstadoRutina.values());
        model.addAttribute("rutinas", new ArrayList<RutinaDto>());
        model.addAttribute("rutinasPorSocio", new LinkedHashMap<Long, List<RutinaDto>>());
        model.addAttribute("resumenSocios", new LinkedHashMap<Long, RutinaDto>());
        model.addAttribute("socios", new LinkedHashMap<Long, String>());
        model.addAttribute("rutinaForm", nuevaRutina(profesorId));

        try {
            List<RutinaDto> rutinas = rutinaService.listarPorProfesor(profesorId);
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

            model.addAttribute("rutinaForm", prepararRutinaForm(profesorId, rutinaId));

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
        }

        return listView;
    }

    @PostMapping("/profesor/{profesorId}")
    public String guardarRutina(@PathVariable Long profesorId,
                                @ModelAttribute("rutinaForm") RutinaDto dto,
                                RedirectAttributes redirectAttributes) {
        dto.setProfesorId(profesorId);
        try {
            if (dto.getId() == null) {
                rutinaService.crear(dto);
                redirectAttributes.addFlashAttribute("msgExito", "Rutina creada correctamente");
            } else {
                rutinaService.actualizar(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("msgExito", "Rutina actualizada correctamente");
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgError", "error.sistema");
        }
        return "redirect:" + redirectProfesor + profesorId;
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

            return tableroProfesor(profesorId, rutinaId, model);

        } catch (BusinessException e) {
            model.addAttribute("msgError", e.getMessageKey());
            return "rutina/list";
        } catch (Exception e) {
            model.addAttribute("msgError", "error.sistema");
            return "rutina/list";
        }
    }

    @PostMapping("/profesor/{profesorId}/rutinas/{rutinaId}/eliminar")
    public String eliminarRutina(@PathVariable Long profesorId,
                                 @PathVariable Long rutinaId,
                                 RedirectAttributes redirectAttributes) {
        try {
            rutinaService.eliminar(rutinaId);
            redirectAttributes.addFlashAttribute("msgExito", "Rutina eliminada correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("msgError", e.getMessageKey());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msgError", "error.sistema");
        }
        return "redirect:" + redirectProfesor + profesorId;
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
        return rutinaService.buscarPorId(rutinaId);
    }

    private RutinaDto nuevaRutina(Long profesorId) {
        RutinaDto rutina = new RutinaDto();
        rutina.setProfesorId(profesorId);
        rutina.setFechaInicio(LocalDate.now());
        rutina.setFechaFinalizacion(LocalDate.now().plusDays(7));
        return rutina;
    }
}
