package org.nttdata.com.servicioprestamos.service.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.nttdata.com.servicioprestamos.client.ClienteClient;
import org.nttdata.com.servicioprestamos.client.CuentaClient;
import org.nttdata.com.servicioprestamos.client.TransaccionClient;
import org.nttdata.com.servicioprestamos.client.dto.*;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.PrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.PrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.BadRequest;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.nttdata.com.servicioprestamos.producer.NotificacionProducer;
import org.nttdata.com.servicioprestamos.producer.dto.ClienteResponseK;
import org.nttdata.com.servicioprestamos.producer.dto.NotificacionRequestK;
import org.nttdata.com.servicioprestamos.repository.PrestamoRepository;
import org.nttdata.com.servicioprestamos.service.CuotaService;
import org.nttdata.com.servicioprestamos.service.PrestamoService;
import org.nttdata.com.servicioprestamos.util.PrestamoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoServiceImpl implements PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final PrestamoMapper prestamoMapper;
    private final ClienteClient clienteClient;
    private final TransaccionClient transaccionClient;
    private final CuentaClient cuentaClient;
    private final CuotaService cuotaService;

    // Kafka
    private final NotificacionProducer notificacionProducer;


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

    @CircuitBreaker(name = "clienteService", fallbackMethod = "clienteFallback")
    public ClienteResponse getClienteById(Long clienteId) {
        try{
            return clienteClient.getClienteById(clienteId);
        } catch (FeignException.NotFound ex){
            throw new ResourceNotFound("Cliente no encontrado con id: " + clienteId);
        }
    }
    public ClienteResponse clienteFallback(Long clienteId, Throwable ex) {
        throw new RuntimeException("FALLBACK: Servicio de clientes no disponible para clienteId=" + clienteId, ex);
    }
    @CircuitBreaker(name = "cuentaService", fallbackMethod = "cuentaFallback")
    public CuentaResponse getCuentaById(Long cuentaId) {
        try{
            return cuentaClient.getCuentaById(cuentaId);
        } catch (FeignException.NotFound ex){
            throw new ResourceNotFound("Cuenta no encontrado con id: " + cuentaId);
        }
    }
    public CuentaResponse cuentaFallback(Long cuentaId, Throwable ex) {
        throw new RuntimeException("FALLBACK: Servicio de cuentas no disponible para cuentaId=" + cuentaId, ex);
    }

    @Override
    @Transactional
    public PrestamoResponse createPrestamo(PrestamoRequest prestamoDto) {

        //Verificar existencia del cliente
        ClienteResponse clienteResponse = getClienteById(prestamoDto.clienteId());
        //Verificacion existencia de cuenta
        CuentaResponse cuentaResponse = getCuentaById(prestamoDto.cuentaId());


        //Evaluar credito
        evalularCredito(prestamoDto.cuentaId(), prestamoDto.monto());
        evaluarMonto(prestamoDto.plazoMeses(), prestamoDto.monto(), prestamoDto.tasaInteres());
        evaluarPlazo(prestamoDto.plazoMeses(), prestamoDto.monto(), prestamoDto.tasaInteres());
        evaluarTasaInteres(prestamoDto.tasaInteres(), prestamoDto.monto(), prestamoDto.plazoMeses());

        //Generar prestamo
        Prestamo prestamo = prestamoMapper.toEntity(prestamoDto);
        //Asignar estado "PENDIENTE" por defecto
        prestamo.setEstadoPrestamo(EstadoPrestamo.builder().id(1L).build());
        //Asignar fecha de desembolso nula por defecto (será asignada al desembolsar el préstamo)
        prestamo.setFechaDesembolso(null);


        //Enviar notificación de creación de préstamo
        notificacionProducer.enviarNotificacion(NotificacionRequestK.builder()
                        .cliente(ClienteResponseK.builder()
                                .id(clienteResponse.id())
                                .nombre(clienteResponse.nombre())
                                .email(clienteResponse.email())
                                .dni(clienteResponse.dni())
                                .estado(clienteResponse.estadoCliente().estado())
                                .build())
                        .tipoNotificacionId(1L) // Tipo de notificación: Préstamo
                        .asunto("Creación de Préstamo")
                        .mensaje("Su solicitud de préstamo por un monto de " + prestamoDto.monto() + " ha sido recibida y está en proceso de evaluación.")
                        .fechaEnvio(new Date())
                        .estadoNotificacionId(1L) // Estado: PENDIENTE
                .build());
        return prestamoMapper.toDto(prestamoRepository.save(prestamo));
    }

    public void evalularCredito(Long cuentaId, BigDecimal montoSolicitado){
        List<TransaccionResponse> transaccionRealizdas = transaccionClient.obteTransacciones(cuentaId);
        BigDecimal ingresos = transaccionRealizdas.stream()
                .filter(t -> t.tipoTransaccion().nombre().equalsIgnoreCase("DEPÓSITO"))
                .map(TransaccionResponse::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal egresos = transaccionRealizdas.stream()
                .filter(t -> {
                    String tipo = t.tipoTransaccion().nombre().toUpperCase();
                    return tipo.equalsIgnoreCase("RETIRO")
                            || tipo.equalsIgnoreCase("PAGO DE SERVICIO")
                            || tipo.equalsIgnoreCase("TRANSFERENCIA");
                })
                .map(TransaccionResponse::monto)
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



    @Override
    @Transactional
    public PrestamoResponse updatePrestamo(Long id, PrestamoRequest prestamoDto) {
        Prestamo prestamoFound = prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );

        //Verificar existencia del cliente
        ClienteResponse clienteResponse = getClienteById(prestamoDto.clienteId());
        //Verificacion existencia de cuenta
        CuentaResponse cuentaResponse = getCuentaById(prestamoDto.cuentaId());

        //Evaluar credito
        evalularCredito(prestamoDto.cuentaId(), prestamoDto.monto());
        evaluarMonto(prestamoDto.plazoMeses(), prestamoDto.monto(), prestamoDto.tasaInteres());
        evaluarPlazo(prestamoDto.plazoMeses(), prestamoDto.monto(), prestamoDto.tasaInteres());
        evaluarTasaInteres(prestamoDto.tasaInteres(), prestamoDto.monto(), prestamoDto.plazoMeses());

        prestamoFound.setClienteId(prestamoDto.clienteId());
        prestamoFound.setCuentaId(prestamoDto.cuentaId());
        prestamoFound.setMonto(prestamoDto.monto());
        prestamoFound.setPlazoMeses(prestamoDto.plazoMeses());
        prestamoFound.setTasaInteres(prestamoDto.tasaInteres());
        prestamoFound.setEstadoPrestamo(prestamoMapper.toEntity(prestamoDto).getEstadoPrestamo());
        prestamoFound.setFechaDesembolso(prestamoDto.fechaDesembolso());
        return prestamoMapper.toDto(prestamoRepository.save(prestamoFound));
    }

    @Override
    public void deletePrestamo(Long id) {
        Prestamo prestamoFound = prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );
        prestamoRepository.delete(prestamoFound);
    }

    @Override
    public List<PrestamoResponse> getPrestamosByClienteId(Long clienteId) {
        //Verificar existencia del cliente
        ClienteResponse clienteResponse = getClienteById(clienteId);

        return prestamoMapper.toDtoList(prestamoRepository.findByClienteId(clienteId));
    }

    @Override
    @Transactional
    public PrestamoResponse aceptarPrestamo(Long id) {
        Prestamo prestamoFound = prestamoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Préstamo no encontrado con id: " + id)
        );


        //Verificar existencia del cliente
        ClienteResponse clienteResponse = getClienteById(prestamoFound.getClienteId());
        //Verificacion existencia de cuenta
        CuentaResponse cuentaResponse = getCuentaById(prestamoFound.getCuentaId());

        //Verificar cantidad de prestamosActivos del cliente
        List<Prestamo> prestamosActivos = prestamoRepository.findByClienteIdAndEstadoPrestamoId(prestamoFound.getClienteId(), 2L);
        if(prestamosActivos.size() >= 3){
            throw new BadRequest("El cliente no puede tener más de 3 préstamos activos.");
        }

        if(prestamoFound.getEstadoPrestamo().getId() != 1L){
            throw new BadRequest("Solo se pueden aceptar préstamos en estado PENDIENTE");
        }
        //Cambiar estado a ACEPTADO (2)
        prestamoFound.setEstadoPrestamo(EstadoPrestamo.builder().id(2L).build());

        //Fecha
        LocalDate hoy = LocalDate.now();


        //Asignar fecha de desembolso
        prestamoFound.setFechaDesembolso(Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        //Realizar deposito en la cuenta asociada al prestamo
        TransaccionRequest transaccionRequest = TransaccionRequest.builder()
                .cuentaId(cuentaResponse.id())
                .tipoTransaccionId(1L)
                .monto(prestamoFound.getMonto())
                .fecha(Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .referencia("Desembolso de préstamo")
                .build();

        transaccionClient.crearTransaccion(transaccionRequest);
        for (int i = 1; i <= prestamoFound.getPlazoMeses(); i++) {
            // Calcular la fecha de vencimiento sumando i meses a la fecha actual
            LocalDate fechaVencimiento = LocalDate.now().plusMonths(i);
            // Calcular el monto de la cuota
            BigDecimal monto = prestamoFound.getMonto()
                    .multiply(prestamoFound.getTasaInteres().add(BigDecimal.ONE))
                    .divide(BigDecimal.valueOf(prestamoFound.getPlazoMeses()), 2, RoundingMode.HALF_UP);
            // Crear y guardar la cuota
            cuotaService.saveCuota(CuotaRequest.builder()
                            .prestamoId(prestamoFound.getId())
                            .numero(i)
                            .fechaVencimiento(Date.from(fechaVencimiento.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                            // Estado de PENDIENTE
                            .estadoCuotaId(1L)
                            .monto(monto)
                            .build());
        }
        // Actualizar saldo de la cuenta
        CuentaRequest cuentaRequest = CuentaRequest.builder()
                .estadoCuentaId(cuentaResponse.estadoCuenta().id())
                .tipoCuentaId(cuentaResponse.tipoCuenta().id())
                .clienteId(cuentaResponse.clienteId())
                .saldo(cuentaResponse.saldo().add(prestamoFound.getMonto()))
                .build();

        cuentaClient.updateCuenta(cuentaResponse.id(), cuentaRequest);


        //Enviar notificación de aceoptacion de préstamo
        notificacionProducer.enviarNotificacion(NotificacionRequestK.builder()
                .cliente(ClienteResponseK.builder()
                        .id(clienteResponse.id())
                        .nombre(clienteResponse.nombre())
                        .email(clienteResponse.email())
                        .dni(clienteResponse.dni())
                        .estado(clienteResponse.estadoCliente().estado())
                        .build())
                .tipoNotificacionId(1L) // Tipo de notificación: Préstamo
                .asunto("Creación de Préstamo")
                .mensaje("Su solicitud de préstamo por un monto de " + prestamoFound.getMonto() + " ha sido aceptada y el monto ha sido depositado en su cuenta.")
                .fechaEnvio(new Date())
                .estadoNotificacionId(1L) // Estado: PENDIENTE
                .build());

        return prestamoMapper.toDto(prestamoRepository.save(prestamoFound));
    }
    //Metodo de vlaidacion de cuenta via feign sin circuit breaker
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
