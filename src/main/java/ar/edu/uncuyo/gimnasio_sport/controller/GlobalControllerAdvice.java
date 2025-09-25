package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.auth.CustomUserDetails;
import ar.edu.uncuyo.gimnasio_sport.entity.Persona;
import ar.edu.uncuyo.gimnasio_sport.service.PersonaService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final PersonaService personaService;

    @Transactional
    @ModelAttribute("currentUser")
    public void addCurrentUser(@AuthenticationPrincipal CustomUserDetails principal, Model model) {
        if (principal != null) {
            try {
                Persona persona = personaService.buscarPersonaActual();
                model.addAttribute("currentUserFullName", persona.getNombre() + " " + persona.getApellido());
                model.addAttribute("currentUserRole", persona.getUsuario().getRol());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}

