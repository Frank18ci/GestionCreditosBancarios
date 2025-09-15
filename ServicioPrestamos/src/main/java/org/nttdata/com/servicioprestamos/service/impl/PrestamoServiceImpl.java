package org.nttdata.com.servicioprestamos.service.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.client.ClienteClient;
import org.nttdata.com.servicioprestamos.client.TransaccionClient;
import org.nttdata.com.servicioprestamos.client.dto.ClienteResponse;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionResponse;
import org.nttdata.com.servicioprestamos.dto.PrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.BadRequest;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.nttdata.com.servicioprestamos.repository.PrestamoRepository;
import org.nttdata.com.servicioprestamos.service.PrestamoService;
import org.nttdata.com.servicioprestamos.util.PrestamoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoServiceImpl implements PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final PrestamoMapper prestamoMapper;
    private final ClienteClient clienteClient;
    private final TransaccionClient transaccionClient;

    private static final String CLIENTE_SERVICE_CB = "clienteService";


    @Override
    public List<PrestamoResponse> getAllPrestamos() {
        return prestamoMapper.toDtoList(prestamoRepository.findAll());
    }

    @Override
    public PrestamoResponse getPrestamoById(Long id) {
        return prestamoMapper.toDto(prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)));
    }

    @Override
    public Prestamo getPrestamoEntityById(Long id) {
        return prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );
    }

    @Override
    @Transactional
    @CircuitBreaker(name = CLIENTE_SERVICE_CB, fallbackMethod = "createPrestamoFallback")
    public PrestamoResponse createPrestamo(PrestamoRequest prestamoDto) {

        //Verificar existencia del cliente
        ClienteResponse clienteResponse;
        try{
            clienteResponse = clienteClient.getClienteById(prestamoDto.getClienteId());
            if(clienteResponse == null || clienteResponse.getId() == null){
                throw new ResourceNotFound("El cliente con id: " + prestamoDto.getClienteId() + " no existe");
            }
        } catch (FeignException.NotFound ex){
            throw new ResourceNotFound("El cliente con id: " + prestamoDto.getClienteId() + " no existe");
        }

        //Verificacion existencia de cuenta
        verificarExistenciaCuenta(prestamoDto.getCuentaId());




        //Evaluar credito
        evalularCredito(prestamoDto.getCuentaId(), prestamoDto.getMonto());
        evaluarMonto(prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTasaInteres());
        evaluarPlazo(prestamoDto.getPlazoMeses(), prestamoDto.getMonto(), prestamoDto.getTasaInteres());
        evaluarTasaInteres(prestamoDto.getTasaInteres(), prestamoDto.getMonto(), prestamoDto.getPlazoMeses());

        //Generar prestamo
        Prestamo prestamo = prestamoMapper.toEntity(prestamoDto);
        //Asignar estado "PENDIENTE" por defecto
        prestamo.setEstadoPrestamo(EstadoPrestamo.builder().id(1L).build());
        //Asignar fecha de desembolso nula por defecto (será asignada al desembolsar el préstamo)
        prestamo.setFechaDesembolso(null);

        return prestamoMapper.toDto(prestamoRepository.save(prestamo));
    }

    public void evalularCredito(Long cuentaId, BigDecimal montoSolicitado){
        List<TransaccionResponse> transaccionRealizdas = transaccionClient.obteTransacciones(cuentaId);
        BigDecimal ingresos = transaccionRealizdas.stream()
                .filter(t -> t.getTipoTransaccion().getNombre().equalsIgnoreCase("DEPÓSITO"))
                .map(TransaccionResponse::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal egresos = transaccionRealizdas.stream()
                .filter(t -> {
                    String tipo = t.getTipoTransaccion().getNombre().toUpperCase();
                    return tipo.equalsIgnoreCase("RETIRO")
                            || tipo.equalsIgnoreCase("PAGO DE SERVICIO")
                            || tipo.equalsIgnoreCase("TRANSFERENCIA");
                })
                .map(TransaccionResponse::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal capacidadPago = ingresos.multiply(new BigDecimal("0.3"));

        if(egresos.compareTo(ingresos.multiply(new BigDecimal("1.1"))) > 0){
            throw new BadRequest("El cliente no cumple con los requisitos para el préstamo porque sus egresos son mayores a sus ingresos");
        } else if(montoSolicitado.compareTo(capacidadPago) > 0){
            throw new BadRequest("El cliente no cumple con los requisitos para el préstamo su maximo es: " + capacidadPago);
        }


    }
    public void evaluarMonto(int plazoMeses, BigDecimal monto, BigDecimal tasaInteres){
        if (monto.compareTo(new BigDecimal("1000")) < 0) {
            throw new IllegalArgumentException("El monto mínimo permitido es 1,000.");
        }
        if (monto.compareTo(new BigDecimal("100000")) > 0) {
            throw new IllegalArgumentException("El monto máximo permitido es 100,000.");
        }
        if (plazoMeses <= 12 && monto.compareTo(new BigDecimal("20000")) > 0) {
            throw new IllegalArgumentException("Para plazos de hasta 12 meses el monto máximo es 20,000.");
        }
        if (plazoMeses >= 36 && monto.compareTo(new BigDecimal("80000")) < 0) {
            throw new IllegalArgumentException("Para plazos de 36 meses o más el monto mínimo es 80,000.");
        }
        if (tasaInteres.compareTo(new BigDecimal("0.25")) > 0 && monto.compareTo(new BigDecimal("50000")) > 0) {
            throw new IllegalArgumentException("No se permiten montos mayores a 50,000 con tasas superiores al 25%.");
        }

        if (tasaInteres.compareTo(new BigDecimal("0.10")) < 0 && monto.compareTo(new BigDecimal("10000")) < 0) {
            throw new IllegalArgumentException("Con tasas menores al 10%, el monto mínimo es 10,000.");
        }
        System.out.println("Monto válido.");
    }
    public void evaluarPlazo(int plazoMeses, BigDecimal monto, BigDecimal tasaInteres){
        if (plazoMeses < 6) {
            throw new IllegalArgumentException("El plazo mínimo permitido es de 6 meses.");
        }
        if (plazoMeses > 60) {
            throw new IllegalArgumentException("El plazo máximo permitido es de 60 meses.");
        }
        if (monto.compareTo(new BigDecimal("10000")) < 0 && plazoMeses > 24) {
            throw new IllegalArgumentException("Para montos menores a 10,000 el plazo máximo es 24 meses.");
        }
        if (tasaInteres.compareTo(new BigDecimal("0.20")) > 0 && plazoMeses > 36) {
            throw new IllegalArgumentException("No se permiten plazos mayores a 36 meses con tasas superiores al 20%.");
        }
        System.out.println("Plazo válido.");

    }
    public void evaluarTasaInteres(BigDecimal tasaInteres, BigDecimal monto, int plazoMeses){
        if (tasaInteres.compareTo(new BigDecimal("0.05")) < 0) {
            throw new IllegalArgumentException("La tasa mínima es del 5%.");
        }
        if (tasaInteres.compareTo(new BigDecimal("0.40")) > 0) {
            throw new IllegalArgumentException("La tasa máxima es del 40%.");
        }
        if (monto.compareTo(new BigDecimal("50000")) > 0 && tasaInteres.compareTo(new BigDecimal("0.15")) < 0) {
            throw new IllegalArgumentException("Para montos mayores a 50,000 la tasa no puede ser menor al 15%.");
        }
        if (plazoMeses > 48 && tasaInteres.compareTo(new BigDecimal("0.30")) > 0) {
            throw new IllegalArgumentException("No se permiten tasas mayores al 30% en plazos superiores a 48 meses.");
        }

        System.out.println("Tasa de interés válida.");
    }

    public PrestamoResponse createPrestamoFallback(PrestamoRequest prestamoDto, Throwable ex) {
        throw new RuntimeException("El servicio de clientes no está disponible. Intente más tarde" + ex);
    }

    @Override
    @CircuitBreaker(name = CLIENTE_SERVICE_CB, fallbackMethod = "createPrestamoFallback")
    public PrestamoResponse updatePrestamo(Long id, PrestamoRequest prestamoDto) {
        Prestamo prestamoFound = prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );

        //Verificar existencia del cliente
        ClienteResponse clienteResponse;
        try{
            clienteResponse = clienteClient.getClienteById(prestamoDto.getClienteId());
            if(clienteResponse == null || clienteResponse.getId() == null){
                throw new ResourceNotFound("El cliente con id: " + prestamoDto.getClienteId() + " no existe");
            }
        } catch (FeignException.NotFound ex){
            throw new ResourceNotFound("El cliente con id: " + prestamoDto.getClienteId() + " no existe");
        }

        prestamoFound.setClienteId(prestamoDto.getClienteId());
        prestamoFound.setCuentaId(prestamoDto.getCuentaId());
        prestamoFound.setMonto(prestamoDto.getMonto());
        prestamoFound.setPlazoMeses(prestamoDto.getPlazoMeses());
        prestamoFound.setTasaInteres(prestamoDto.getTasaInteres());
        prestamoFound.setEstadoPrestamo(prestamoMapper.toEntity(prestamoDto).getEstadoPrestamo());
        prestamoFound.setFechaDesembolso(prestamoDto.getFechaDesembolso());
        return prestamoMapper.toDto(prestamoRepository.save(prestamoFound));
    }

    @Override
    public void deletePrestamo(Long id) {
        Prestamo prestamoFound = prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );
        prestamoRepository.delete(prestamoFound);
    }

    private void verificarExistenciaCuenta(Long cuentaId) {
        if (cuentaId == null) {
            throw new BadRequest("El id de la cuenta no puede ser nulo");
        }
        try {
            List<TransaccionResponse> transacciones = transaccionClient.obteTransacciones(cuentaId);
            // Si el cliente de transacciones devuelve null o lanza NotFound, lo tratamos como inexistente
            if (transacciones == null) {
                throw new ResourceNotFound("La cuenta con id: " + cuentaId + " no existe");
            }
        } catch (FeignException.NotFound nf) {
            throw new ResourceNotFound("La cuenta con id: " + cuentaId + " no existe");
        } catch (FeignException fe) {
            throw new RuntimeException("No se pudo verificar la cuenta: " + fe.getMessage(), fe);
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar existencia de la cuenta: " + ex.getMessage(), ex);
        }
    }
}
