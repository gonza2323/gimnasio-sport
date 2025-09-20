package ar.edu.uncuyo.gimnasio_sport.service;

import ar.edu.uncuyo.gimnasio_sport.dto.FacturaDto;
import ar.edu.uncuyo.gimnasio_sport.entity.Factura;
import ar.edu.uncuyo.gimnasio_sport.error.BusinessException;
import ar.edu.uncuyo.gimnasio_sport.mapper.FacturaMapper;
import ar.edu.uncuyo.gimnasio_sport.repository.FacturaRepository;
import ar.edu.uncuyo.gimnasio_sport.repository.TipoDePagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;
    private final FacturaMapper facturaMapper;
    private final TipoDePagoRepository tipoDePagoRepository;

    public Factura crearFactura(FacturaDto facturaDto) {
        if (facturaRepository.existsByNumeroFactura(facturaDto.getNumeroFactura())) {
            throw new BusinessException("yaExiste.factura.numero");
        }
        if (!tipoDePagoRepository.existsByTipoDePago(facturaDto.getFormaDePago().getTipoDePago())) {
            throw new BusinessException("noExiste.tipoDePago");
        }

        Factura factura = facturaMapper.toEntity(facturaDto);
        return facturaRepository.save(factura);
    }
}
