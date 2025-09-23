package ar.edu.uncuyo.gimnasio_sport.init;

import ar.edu.uncuyo.gimnasio_sport.dto.*;
import ar.edu.uncuyo.gimnasio_sport.entity.Empresa;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoCuota;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoDocumento;
import ar.edu.uncuyo.gimnasio_sport.enums.TipoEmpleado;
import ar.edu.uncuyo.gimnasio_sport.repository.EmpresaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.UsuarioRepository;
import ar.edu.uncuyo.gimnasio_sport.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitialization implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PaisService paisService;
    private final EmpresaRepository empresaRepository;
    private final SucursalService sucursalService;
    private final ProvinciaService provinciaService;
    private final DepartamentoService departamentoService;
    private final LocalidadService localidadService;
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
    protected void crearDatosIniciales() {
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoFalse("admin")) {
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
        crearProvincias();
        crearDepartamentos();
        crearLocalidades();
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
        paisService.crearPais(new PaisDto(null, "Argentina"));
        paisService.crearPais(new PaisDto(null, "España"));
    }

    void crearProvincias() {
        provinciaService.crearProvincia(new ProvinciaDto(null, "CABA", 1L));
        provinciaService.crearProvincia(new ProvinciaDto(null, "Mendoza", 1L));
        provinciaService.crearProvincia(new ProvinciaDto(null, "Barcelona", 2L));
    }

    void crearDepartamentos() {
        departamentoService.crearDepartamento(new DepartamentoDto(null, "Comuna 1", 1L));
        departamentoService.crearDepartamento(new DepartamentoDto(null, "Ciudad de Mendoza", 2L));
        departamentoService.crearDepartamento(new DepartamentoDto(null, "Barcelona", 3L));
    }

    void crearLocalidades() {
        localidadService.crearLocalidad(new LocalidadDto(null, "Monserrat", "1234", 1L));
        localidadService.crearLocalidad(new LocalidadDto(null, "San Telmo", "1234", 1L));
        localidadService.crearLocalidad(new LocalidadDto(null, "1A. Sección", "1234", 2L));
        localidadService.crearLocalidad(new LocalidadDto(null, "2A. Sección", "1234", 2L));
        localidadService.crearLocalidad(new LocalidadDto(null, "Barrio ASDF", "1234", 3L));
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
                        .localidadId(1L)
                        .build())
                .build());

        sucursalService.crearSucursal(SucursalDto.builder()
                .nombre("CABA 2")
                .direccion(DireccionDto.builder()
                        .calle("Av. Corrientes")
                        .numeracion("276")
                        .localidadId(2L)
                        .build())
                .build());

        sucursalService.crearSucursal(SucursalDto.builder()
                .nombre("Mendoza 1")
                .direccion(DireccionDto.builder()
                        .calle("Av. Emilio Civit")
                        .numeracion("1020")
                        .localidadId(3L)
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
                        .localidadId(1L)
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
                        .localidadId(1L)
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
                        .localidadId(2L)
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
                        .localidadId(3L)
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
                        .apellido("admin")
                        .fechaNacimiento(LocalDate.now())
                        .tipoDocumento(TipoDocumento.DNI)
                        .numeroDocumento("25468231")
                        .telefono("11 5486 9235")
                        .correoElectronico("admin")
                        .direccion(DireccionDto.builder()
                                .calle("Av. 9 de Julio")
                                .numeracion("400")
                                .localidadId(1L)
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
                                .localidadId(1L)
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
                .valorCuota(30000d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2025, 6, 30))
                .fechaDesde(LocalDate.of(2025, 1, 1))
                .valorCuota(20000d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2024, 12, 31))
                .fechaDesde(LocalDate.of(2024, 7, 1))
                .valorCuota(10000d)
                .build());

        valorCuotaService.crearValorCuota(ValorCuotaDto.builder()
                .fechaHasta(LocalDate.of(2024, 6, 30))
                .fechaDesde(LocalDate.of(2024, 1, 1))
                .valorCuota(5000d)
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
                .estado(EstadoCuota.PAGADA)
                .fechaVencimiento(LocalDate.of(2025, 5, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.APRIL)
                .estado(EstadoCuota.PAGADA)
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
                .estado(EstadoCuota.PAGADA)
                .fechaVencimiento(LocalDate.of(2025, 2, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2025L)
                .mes(Month.JANUARY)
                .estado(EstadoCuota.PAGADA)
                .fechaVencimiento(LocalDate.of(2025, 1, 10))
                .valorCuotaId(2L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2024L)
                .mes(Month.DECEMBER)
                .estado(EstadoCuota.PAGADA)
                .fechaVencimiento(LocalDate.of(2024, 12, 10))
                .valorCuotaId(3L)
                .socioId(1L)
                .build());

        cuotaMensualService.crearCuotaMensual(CuotaMensualCreateDto.builder()
                .anio(2024L)
                .mes(Month.NOVEMBER)
                .estado(EstadoCuota.PAGADA)
                .fechaVencimiento(LocalDate.of(2024, 11, 10))
                .valorCuotaId(3L)
                .socioId(1L)
                .build());
    }
}
