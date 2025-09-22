package ar.edu.uncuyo.gimnasio_sport.controller;

import ar.edu.uncuyo.gimnasio_sport.dto.MensajeDTO;
import ar.edu.uncuyo.gimnasio_sport.entity.Mensaje;
import ar.edu.uncuyo.gimnasio_sport.entity.Usuario;
import ar.edu.uncuyo.gimnasio_sport.enums.RolUsuario;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoMensaje;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.MensajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MensajeController.class)
class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MensajeService mensajeService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario admin;

    @BeforeEach
    void setUp() {
        admin = Usuario.builder()
                .id(1L)
                .nombreUsuario("admin")
                .clave("secret")
                .rol(RolUsuario.ADMINISTRATIVO)
                .eliminado(false)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATIVO"})
    void mostrarFormularioDeNuevoMensaje() throws Exception {
        when(usuarioRepository.findByNombreUsuarioAndEliminadoFalse("admin")).thenReturn(Optional.of(admin));

        mockMvc.perform(get("/mensajes/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("view/mensaje/form"))
                .andExpect(model().attributeExists("mensaje"))
                .andExpect(model().attribute("usuarioActual", samePropertyValuesAs(admin)))
                .andExpect(model().attribute("mensaje", hasProperty("usuarioId", equalTo(admin.getId()))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATIVO"})
    void editarMensajeMuestraDatosDelMensaje() throws Exception {
        when(usuarioRepository.findByNombreUsuarioAndEliminadoFalse("admin")).thenReturn(Optional.of(admin));

        Usuario autor = Usuario.builder()
                .id(2L)
                .nombreUsuario("autor")
                .clave("pwd")
                .rol(RolUsuario.PROFESOR)
                .eliminado(false)
                .build();

        Long mensajeId = 42L;

        Mensaje mensaje = new Mensaje();
        mensaje.setId(mensajeId);
        mensaje.setTitulo("Titulo");
        mensaje.setTexto("Contenido");
        mensaje.setTipoMensaje(TipoMensaje.PROMOCION);
        mensaje.setEliminado(false);
        mensaje.setUsuario(autor);

        MensajeDTO dto = new MensajeDTO(mensajeId, "Titulo", "Contenido", TipoMensaje.PROMOCION, autor.getId());

        when(mensajeService.obtener(mensajeId)).thenReturn(mensaje);
        when(mensajeService.toDto(mensaje)).thenReturn(dto);

        mockMvc.perform(get("/mensajes/editar/{id}", mensajeId))
                .andExpect(status().isOk())
                .andExpect(view().name("view/mensaje/form"))
                .andExpect(model().attribute("usuarioActual", samePropertyValuesAs(admin)))
                .andExpect(model().attribute("mensaje", samePropertyValuesAs(dto)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATIVO"})
    void crearMensajeRedirigeAlListado() throws Exception {
        when(usuarioRepository.findByNombreUsuarioAndEliminadoFalse("admin")).thenReturn(Optional.of(admin));
        when(mensajeService.crear(any(MensajeDTO.class))).thenReturn(new Mensaje());

        mockMvc.perform(post("/mensajes")
                        .param("titulo", "Promo")
                        .param("texto", "Descuento especial")
                        .param("tipoMensaje", TipoMensaje.PROMOCION.name())
                        .param("usuarioId", "999")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mensajes"));

        verify(mensajeService).crear(argThat(dto -> admin.getId().equals(dto.getUsuarioId())));
    }
}
