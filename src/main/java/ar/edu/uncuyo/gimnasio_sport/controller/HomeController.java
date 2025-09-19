package ar.edu.uncuyo.gimnasio_sport.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping({"/", "/about", "/blog", "/contact", "/elements", "/gallery", "/pricing", "/single-blog"})
    public String page(Principal principal, HttpServletRequest request) {
        String pageName = request.getRequestURI().substring(1);
        return pageName.isEmpty() ? "home" : pageName;
    }
}
