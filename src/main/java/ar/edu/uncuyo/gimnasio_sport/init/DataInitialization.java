package ar.edu.uncuyo.gimnasio_sport.init;

import ar.edu.uncuyo.gimnasio_sport.dto.*;
import ar.edu.uncuyo.gimnasio_sport.entity.*;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoDocumento;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.init.geo.*;
import ar.edu.uncuyo.gimnasio_sport.repository.*;
import ar.edu.uncuyo.gimnasio_sport.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final LocalidadRepository localidadRepository;
    private final PaisRepository paisRepository;
    private final UsuarioRepository usuarioRepository;
    private final PaisService paisService;
    private final EmpresaRepository empresaRepository;
    private final SucursalService sucursalService;
    private final SocioService socioService;
    private final EmpleadoService empleadoService;
    private final ValorCuotaService valorCuotaService;
    private final CuotaMensualService cuotaMensualService;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        crearDatosIniciales();
    }

    @Transactional
    protected void crearDatosIniciales() throws Exception {
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse("pepeargento@gmail.com")) {
            System.out.println("Datos iniciales ya creados. Salteando creación de datos iniciales. Para forzar su creación, borrar la base de datos");
            return;
        }

        // Nos damos permisos para poder crear los datos iniciales
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRATIVO"));
        var auth = new UsernamePasswordAuthenticationToken("system", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("Creando datos iniciales...");

        // Creación de datos iniciales
        crearPaises();
        cargarUbicaciones();
        crearEmpresa();
        crearSucursales();
        crearSocios();
        crearEmpleados();
        crearValoresCuota();
        crearCuotas();

        // Resetear los permisos
        SecurityContextHolder.clearContext();

        System.out.println("Datos iniciales creados.");
    }

    private void crearPaises() {
        paisService.crearPais(new PaisDto(1L, "Argentina"));
        paisService.crearPais(new PaisDto(2L, "España"));
    }

    private void cargarUbicaciones() throws Exception {
        Pais argentina = paisRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Argentina not found"));

        loadProvincias(argentina);
        loadDepartamentos();
        loadLocalidades();
    }

    private void loadProvincias(Pais argentina) throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/provincias.json");
        ProvinciasWrapper wrapper = objectMapper.readValue(is, ProvinciasWrapper.class);

        for (ProvinciaDTO dto : wrapper.getProvincias()) {
            Long id = dto.getIdAsLong();
            if (!provinciaRepository.existsById(id)) {
                Provincia provincia = new Provincia();
                provincia.setId(id);
                provincia.setNombre(dto.getNombre());
                provincia.setPais(argentina);
                provinciaRepository.save(provincia);
            }
        }
    }

    private void loadDepartamentos() throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/departamentos.json");
        DepartamentosWrapper wrapper = objectMapper.readValue(is, DepartamentosWrapper.class);

        for (DepartamentoDTO dto : wrapper.getDepartamentos()) {
            Long id = dto.getIdAsLong();
            if (!departamentoRepository.existsById(id)) {
                Long provinciaId = dto.getProvincia().getIdAsLong();
                Provincia provincia = provinciaRepository.findById(provinciaId)
                        .orElseThrow(() -> new IllegalStateException("Provincia not found: " + provinciaId));

                Departamento departamento = new Departamento();
                departamento.setId(id);
                departamento.setNombre(dto.getNombre());
                departamento.setProvincia(provincia);
                departamentoRepository.save(departamento);
            }
        }
    }

    private void loadLocalidades() throws IOException {
        InputStream is = getClass().getResourceAsStream("/data/localidades.json");
        LocalidadesWrapper wrapper = objectMapper.readValue(is, LocalidadesWrapper.class);

        for (LocalidadDTO dto : wrapper.getLocalidades()) {
            Long id = dto.getIdAsLong();
            if (!localidadRepository.existsById(id)) {
                Long departamentoId = dto.getDepartamento().getIdAsLong();
                Departamento departamento = departamentoRepository.findById(departamentoId)
                        .orElseThrow(() -> new IllegalStateException("Departamento not found: " + departamentoId));

                Localidad localidad = new Localidad();
                localidad.setId(id);
                localidad.setNombre(dto.getNombre());
                localidad.setDepartamento(departamento);
                localidadRepository.save(localidad);
            }
        }
    }

    private void crearEmpresa() {
        Empresa empresa = new Empresa(null, "Gimnasio Sport", "+54 9 11 2216-2867", "contacto@gymsport.com", false);
        empresaRepository.save(empresa);
    }

    private void crearSucursales() {
        sucursalService.crearSucursal(SucursalDto.builder()
                .nombre("CABA 1")
                .direccion(DireccionDto.builder()
                        .calle("Av. 9 de Julio")
                        .numeracion("870")
                        .localidadId(200701001L)
                        .build())
                .build());

        sucursalService.crearSucursal(SucursalDto.builder()
                .nombre("CABA 2")
                .direccion(DireccionDto.builder()
                        .calle("Av. Corrientes")
                        .numeracion("276")
                        .localidadId(200701002L)
                        .build())
                .build());

        sucursalService.crearSucursal(SucursalDto.builder()
                .nombre("Mendoza 1")
                .direccion(DireccionDto.builder()
                        .calle("Av. Emilio Civit")
                        .numeracion("1020")
                        .localidadId(5000701001L)
                        .build())
                .build());
    }

    private void crearSocios() {
        socioService.crearSocio(SocioCreateFormDto.builder()
                .nombre("Pepe")
                .apellido("Argento")
                .fechaNacimiento(LocalDate.now())
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .telefono("11 1245 5748")
                .correoElectronico("pepeargento@gmail.com")
                .direccion(DireccionDto.builder()
                        .calle("Av. pepito")
                        .numeracion("42")
                        .localidadId(200701001L)
                        .build())
                .usuario(UsuarioCreateFormDTO.builder()
                        .clave("1234")
                        .confirmacionClave("1234")
                        .build())
                .sucursalId(1L)
                .build());

        socioService.crearSocio(SocioCreateFormDto.builder()
                .nombre("Moni")
                .apellido("Argento")
                .fechaNacimiento(LocalDate.now())
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("23456789")
                .telefono("11 1548 6782")
                .correoElectronico("moniargento@gmail.com")
                .direccion(DireccionDto.builder()
                        .calle("Av. pepito")
                        .numeracion("42")
                        .localidadId(200701001L)
                        .build())
                .usuario(UsuarioCreateFormDTO.builder()
                        .clave("1234")
                        .confirmacionClave("1234")
                        .build())
                .sucursalId(1L)
                .build());

        socioService.crearSocio(SocioCreateFormDto.builder()
                .nombre("Alberto")
                .apellido("Fernandez")
                .fechaNacimiento(LocalDate.now())
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("54862135")
                .telefono("11 5814 6502")
                .correoElectronico("albertofernandez@gmail.com")
                .direccion(DireccionDto.builder()
                        .calle("Av. San Telmo")
                        .numeracion("42")
                        .localidadId(200701002L)
                        .build())
                .usuario(UsuarioCreateFormDTO.builder()
                        .clave("1234")
                        .confirmacionClave("1234")
                        .build())
                .sucursalId(2L)
                .build());

        socioService.crearSocio(SocioCreateFormDto.builder()
                .nombre("Julio")
                .apellido("Cobos")
                .fechaNacimiento(LocalDate.now())
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("32165498")
                .telefono("261 584 8534")
                .correoElectronico("juliocobos@gmail.com")
                .direccion(DireccionDto.builder()
                        .calle("Av. Colón")
                        .numeracion("252")
                        .localidadId(5000701001L)
                        .build())
                .usuario(UsuarioCreateFormDTO.builder()
                        .clave("1234")
                        .confirmacionClave("1234")
                        .build())
                .sucursalId(3L)
                .build());
    }

    private void crearEmpleados() {
        empleadoService.crearEmpleado(EmpleadoCreateForm.builder()
                .tipoEmpleado(TipoEmpleado.ADMINISTRATIVO)
                .persona(PersonaCreateFormDTO.builder()
                        .nombre("admin")
                        .apellido("")
                        .fechaNacimiento(LocalDate.now())
                        .tipoDocumento(TipoDocumento.DNI)
                        .numeroDocumento("25468231")
                        .telefono("11 5486 9235")
                        .correoElectronico("gimnasiosport21@gmail.com")
                        .direccion(DireccionDto.builder()
                                .calle("Av. 9 de Julio")
                                .numeracion("400")
                                .localidadId(200701001L)
                                .build())
                        .usuario(UsuarioCreateFormDTO.builder()
                                .clave("1234")
                                .confirmacionClave("1234")
                                .build())
                        .sucursalId(1L)
                        .build()
                ).build());

        empleadoService.crearEmpleado(EmpleadoCreateForm.builder()
                .tipoEmpleado(TipoEmpleado.PROFESOR)
                .persona(PersonaCreateFormDTO.builder()
                        .nombre("jackie")
                        .apellido("chan")
                        .fechaNacimiento(LocalDate.now())
                        .tipoDocumento(TipoDocumento.DNI)
                        .numeroDocumento("25124653")
                        .telefono("11 5124 0215")
                        .correoElectronico("jackiechan@gmail.com")
                        .direccion(DireccionDto.builder()
                                .calle("Av. Cabildo")
                                .numeracion("568")
                                .localidadId(200701001L)
                                .build())
                        .usuario(UsuarioCreateFormDTO.builder()
                                .clave("1234")
                                .confirmacionClave("1234")
                                .build())
                        .sucursalId(1L)
                        .build()
                ).build());
    }

    private void crearValoresCuota() {
        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2025, 12, 31))
                .fechaDesde(LocalDate.of(2025, 7, 1))
                .valorCuota(30d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2025, 6, 30))
                .fechaDesde(LocalDate.of(2025, 1, 1))
                .valorCuota(20d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2024, 12, 31))
                .fechaDesde(LocalDate.of(2024, 7, 1))
                .valorCuota(10d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2024, 6, 30))
                .fechaDesde(LocalDate.of(2024, 1, 1))
                .valorCuota(5d)
                .build());
    }

    private void crearCuotas() {
        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.AUGUST)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 8, 10))
                .valorCuotaId(1L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.JULY)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 7, 10))
                .valorCuotaId(1L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.JUNE)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 6, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.MAY)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 5, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.APRIL)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 4, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.MARCH)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 3, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.FEBRUARY)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 2, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.JANUARY)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2025, 1, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2024L)
                .mes(Month.DECEMBER)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2024, 12, 10))
                .valorCuotaId(3L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2024L)
                .mes(Month.NOVEMBER)
                .estado(EstadoCuota.ADEUDADA)
                .fechaVencimiento(LocalDate.of(2024, 11, 10))
                .valorCuotaId(3L)
                .socioId(1L)
                .build());
    }
}
