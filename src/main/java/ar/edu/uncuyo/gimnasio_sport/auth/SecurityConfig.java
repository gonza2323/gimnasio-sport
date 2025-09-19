package ar.edu.uncuyo.gimnasio_sport.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/fonts/**", "/img/**", "/js/**").permitAll()
                        .requestMatchers("/", "/about", "/blog", "/contact", "/elements",
                                "gallery", "pricing", "single-blog", "/error").permitAll()
                        .requestMatchers("/paises/**").hasRole("ADMINISTRATIVO")
                        .anyRequest().authenticated()
                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                        .defaultSuccessUrl("/", true)    // redirect after successful login
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .cors(cors -> cors.disable()
//                        .configurationSource(request -> {
//                            var cors = new org.springframework.web.cors.CorsConfiguration();
//                            cors.setAllowedOrigins(java.util.List.of("http://localhost:8080"));
//                            cors.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE"));
//                            cors.setAllowedHeaders(java.util.List.of("*"));
//                            return cors;
//                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
