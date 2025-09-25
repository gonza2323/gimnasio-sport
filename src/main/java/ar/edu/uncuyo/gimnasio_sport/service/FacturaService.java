package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.DetalleFacturaDto;
import ar.edu.uncuyo.gimnasio_sport.dto.FacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.CuotaMensual;
import ar.edu.uncuyo.gimnasio_sport.entity.DetalleFactura;
import ar.edu.uncuyo.gimnasio_sport.entity.Factura;
import ar.edu.uncuyo.gimnasio_sport.entity.FormaDePago;
import ar.edu.uncuyo.gimnasio_sport.enums.EstadoFactura;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.DetalleFacturaMapper;
import ar.edu.uncuyo.gimnasio_sport.mapper.FacturaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.CuotaMensualRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.DetalleFacturaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.FacturaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.TipoDePagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final FacturaMapper facturaMapper;
    private final TipoDePagoRepository tipoDePagoRepository;
    private final CuotaMensualRepository cuotaMensualRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final DetalleFacturaMapper detalleFacturaMapper;


    @Transactional
    public List<FacturaDto> listarFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        return facturaMapper.toDtos(facturas);
    }

    @Transactional
    public List<FacturaDto> listarFacturasActivas() {
        List<Factura> facturas = facturaRepository.findAllByEliminadoFalseOrderByFechaFacturaDescNumeroFacturaDesc();
        return facturaMapper.toDtos(facturas);
    }

    @Transactional
    public List<FacturaDto> listarFacturasPorEstado(EstadoFactura estado) {
        List<Factura> facturas = facturaRepository.findAllByEliminadoFalseAndEstado(estado);
        return facturaMapper.toDtos(facturas);
    }

    @Transactional
    public DetalleFacturaDto crearDetalle(Long facturaId, Long cuotaMensualId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new BusinessException("factura.noEncontrada"));

        CuotaMensual cuotaMensual = cuotaMensualRepository.findById(cuotaMensualId)
                .orElseThrow(() -> new BusinessException("cuotaMensual.noEncontrada"));

        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(factura);
        detalle.setCuotaMensual(cuotaMensual);
        detalle.setEliminado(false);

        DetalleFactura detalleGuardado = detalleFacturaRepository.save(detalle);

        return detalleFacturaMapper.toDto(detalleGuardado);
    }

    @Transactional
    public DetalleFacturaDto buscarDetalle(Long id) {
        DetalleFactura detalle = detalleFacturaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("detalle.noEncontrado"));
        return detalleFacturaMapper.toDto(detalle);
    }

    @Transactional
    public DetalleFacturaDto modificarDetalle(Long idDetalle, Long idCuota) {
        DetalleFactura detalle = detalleFacturaRepository.findById(idDetalle)
                .orElseThrow(() -> new BusinessException("detalle.noEncontrado"));

        CuotaMensual cuotaMensual = cuotaMensualRepository.findById(idCuota)
                .orElseThrow(() -> new BusinessException("cuotaMensual.noEncontrada"));

        detalle.setCuotaMensual(cuotaMensual);

        DetalleFactura detalleActualizado = detalleFacturaRepository.save(detalle);

        return detalleFacturaMapper.toDto(detalleActualizado);
    }

    @Transactional
    public void eliminarDetalle(Long idDetalle) {
        DetalleFactura detalle = detalleFacturaRepository.findById(idDetalle)
                .orElseThrow(() -> new BusinessException("detalle.noEncontrado"));

        detalle.setEliminado(true);
        detalleFacturaRepository.save(detalle);
    }

    @Transactional
    public Factura crearFactura(FacturaDto facturaDto) {
        FormaDePago formaDePago = FormaDePago.builder()
                .tipoDePago(facturaDto.getFormaDePago().getTipoDePago())
                .observacion(facturaDto.getFormaDePago().getObservacion())
                .build();

        Factura factura = facturaMapper.toEntity(facturaDto);
        factura.setFormaDePago(formaDePago);
        factura.setNumeroFactura(generarSiguienteNumeroDeFactura());
        factura.setEliminado(false);
        facturaRepository.save(factura);

//        Detalles
        List<DetalleFactura> detalles = facturaDto.getDetalles().stream().map(dto -> {
            CuotaMensual cuotaMensual = cuotaMensualRepository.findByIdAndEliminadoFalse(dto.getCuotaMensualId())
                    .orElseThrow();

            return DetalleFactura.builder()
                    .factura(factura)
                    .cuotaMensual(cuotaMensual)
                    .build();
        }).toList();

        detalleFacturaRepository.saveAll(detalles);

        return factura;
    }

    private Long generarSiguienteNumeroDeFactura() {
        Long max = facturaRepository.findMaxNumeroFactura();
        return max == null ? 1L : max + 1;
    }

    public void validar(FacturaDto facturaDto) {
        if (facturaRepository.existsByNumeroFactura(facturaDto.getNumeroFactura())) {
            throw new BusinessException("YaExiste.factura.numero");
        }
        if (!tipoDePagoRepository.existsByTipoDePago(facturaDto.getFormaDePago().getTipoDePago())) {
            throw new BusinessException("NoExiste.tipoDePago");
        }
    }

    @Transactional
    public Factura buscarFactura(Long id) {
        return facturaRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new BusinessException("Factura no encontrada"));
    }

    @Transactional
    public FacturaDto buscarFacturaDto(Long id) {
        Factura factura = buscarFactura(id);
        FacturaDto facturaDto = facturaMapper.toDto(factura);
        return facturaDto;
    }

    @Transactional
    public Factura eliminarFactura(Long id) {
        Factura factura = buscarFactura(id);
        factura.setEliminado(true);
        return facturaRepository.save(factura);
    }

    @Transactional
    public Factura modificarFactura(Long id, FacturaDto facturaDto) {
        Factura factura = buscarFactura(id);

        validar(facturaDto);

        if (!factura.getNumeroFactura().equals(facturaDto.getNumeroFactura())) {
            throw new BusinessException("noPermitido.factura.numeroNoEditable");
        }

        if (!factura.getFechaFactura().equals(facturaDto.getFechaFactura())) {
            throw new BusinessException("noPermitido.factura.fechaNoEditable");
        }

        facturaMapper.updateFromDto(facturaDto, factura);
        return facturaRepository.save(factura);
    }

    @Transactional
    public Long buscarIdFacturaPorCuotaId(Long cuotaId) {
        Factura factura = facturaRepository.findFacturaByCuotaId(cuotaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return factura.getId();
    }
}
