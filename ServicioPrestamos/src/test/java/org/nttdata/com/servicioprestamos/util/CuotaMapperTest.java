package org.nttdata.com.servicioprestamos.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nttdata.com.servicioprestamos.dto.CuotaRequest;
import org.nttdata.com.servicioprestamos.dto.CuotaResponse;
import org.nttdata.com.servicioprestamos.models.Cuota;
import org.nttdata.com.servicioprestamos.models.EstadoCuota;
import org.nttdata.com.servicioprestamos.models.Prestamo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CuotaMapperTest {
    @Autowired
    private CuotaMapper cuotaMapper;

    @Test
    void toEntity_mapsRequestToEntity() {
        CuotaRequest request = CuotaRequest.builder()
                .estadoCuotaId(1L)
                .prestamoId(2L)
                .monto(BigDecimal.valueOf(500))
                .numero(1)
                .fechaVencimiento(new Date())
                .build();

        Cuota entity = cuotaMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(1L, entity.getEstadoCuota().getId());
        assertEquals(2L, entity.getPrestamo().getId());
        assertEquals(BigDecimal.valueOf(500), entity.getMonto());
    }

    @Test
    void toDto_mapsEntityToDto() {
        Cuota cuota = Cuota.builder()
                .id(10L)
                .monto(BigDecimal.valueOf(1000))
                .numero(3)
                .fechaVencimiento(new Date())
                .estadoCuota(EstadoCuota.builder().id(1L).nombre("PENDIENTE").build())
                .prestamo(Prestamo.builder().id(20L).build())
                .build();

        CuotaResponse dto = cuotaMapper.toDto(cuota);

        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals(BigDecimal.valueOf(1000), dto.monto());
        assertEquals(3, dto.numero());
        assertEquals(20L, dto.prestamo().id());
        assertEquals(1L, dto.estadoCuota().id());
    }

    @Test
    void toDtoList_mapsListCorrectly() {
        List<Cuota> cuotas = List.of(
                Cuota.builder().id(1L).monto(BigDecimal.valueOf(100)).build(),
                Cuota.builder().id(2L).monto(BigDecimal.valueOf(200)).build()
        );

        List<CuotaResponse> responses = cuotaMapper.toDtoList(cuotas);

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(200, responses.get(1).monto().intValue());
    }
}
