package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.auth.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    @GetMapping
    public String home() {
        return "view/home";
    }

    @ResponseBody
    @GetMapping("/me")
    public CustomUserDetails userDetails(@AuthenticationPrincipal CustomUserDetails authenticatedUserDetails) {
        System.out.println(authenticatedUserDetails);
        return authenticatedUserDetails;
    }
}
