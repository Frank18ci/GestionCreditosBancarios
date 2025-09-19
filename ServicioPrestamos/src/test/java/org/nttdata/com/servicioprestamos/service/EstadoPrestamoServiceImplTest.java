package org.nttdata.com.servicioprestamos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoRequest;
import org.nttdata.com.servicioprestamos.dto.EstadoPrestamoResponse;
import org.nttdata.com.servicioprestamos.exception.ResourceNotFound;
import org.nttdata.com.servicioprestamos.models.EstadoPrestamo;
import org.nttdata.com.servicioprestamos.repository.EstadoPrestamoRepository;
import org.nttdata.com.servicioprestamos.util.EstadoPrestamoMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstadoPrestamoServiceImplTest {
    @Mock
    private EstadoPrestamoRepository estadoPrestamoRepository;

    @Mock
    private EstadoPrestamoMapper estadoPrestamoMapper;

    @InjectMocks
    private EstadoPrestamoServiceImpl estadoPrestamoService;

    @Test
    @DisplayName("getAllEstadosPrestamo returns list of EstadoPrestamoDto")
    void getAllEstadosPrestamo_ReturnsListOfEstadoPrestamoDto() {
        List<EstadoPrestamo> estados = List.of(
                EstadoPrestamo
                        .builder()
                        .id(1L)
                        .nombre("Aprobado")
                        .build(),
                EstadoPrestamo
                        .builder()
                        .id(2L)
                        .nombre("Rechazado")
                        .build());
        List<EstadoPrestamoResponse> estadosDto = List.of(
                EstadoPrestamoResponse
                        .builder()
                        .id(1L)
                        .nombre("Aprobado")
                        .build(),
                EstadoPrestamoResponse
                        .builder()
                        .id(2L)
                        .nombre("Rechazado")
                        .build());

        when(estadoPrestamoRepository.findAll()).thenReturn(estados);
        when(estadoPrestamoMapper.toDtoList(estados)).thenReturn(estadosDto);
        List<EstadoPrestamoResponse> result = estadoPrestamoService.getAllEstadosPrestamo();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Aprobado", result.getFirst().nombre()),
                () -> assertEquals("Rechazado", result.get(1).nombre())
        );

        verify(estadoPrestamoRepository).findAll();
        verify(estadoPrestamoMapper).toDtoList(estados);
    }
    @Test
    @DisplayName("getEstadoPrestamoById with existing id returns EstadoPrestamoDto")
    void getEstadoPrestamoById_ExistingId_ReturnsEstadoPrestamoDto() {
        Long id = 1L;
        EstadoPrestamo estado = EstadoPrestamo.builder().id(id).nombre("Aprobado").build();
        EstadoPrestamoResponse estadoDto = EstadoPrestamoResponse
                .builder()
                .id(id)
                .nombre("Aprobado")
                .build();


        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.of(estado));

        when(estadoPrestamoMapper.toDto(estado)).thenReturn(estadoDto);

        EstadoPrestamoResponse result = estadoPrestamoService.getEstadoPrestamoById(id);
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("Aprobado", result.nombre())
        );
        verify(estadoPrestamoRepository).findById(id);
        verify(estadoPrestamoMapper).toDto(estado);
    }
    @Test
    @DisplayName("getEstadoPrestamoById with non-existing id throws ResourceNotFound")
    void getEstadoPrestamoById_NonExistingId_ThrowsResourceNotFound() {
        Long id = 1L;
        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoPrestamoService.getEstadoPrestamoById(id));
        assertEquals("Estado de préstamo no encontrado con id: " + id, exception.getMessage());
        verify(estadoPrestamoRepository).findById(id);
    }
    @Test
    @DisplayName("crear EstadoPrestamo saves and returns EstadoPrestamoDto")
    void createEstadoPrestamo_SavesAndReturnsEstadoPrestamoDto() {
        EstadoPrestamoRequest estadoPrestamoRequest = EstadoPrestamoRequest
                .builder()
                .nombre("Aprobado")
                .build();
        EstadoPrestamoResponse estadoDto = EstadoPrestamoResponse
                .builder()
                .id(1L)
                .nombre("Aprobado")
                .build();
        EstadoPrestamo estado = EstadoPrestamo.builder().id(1L).nombre("Aprobado").build();
        when(estadoPrestamoMapper.toEntity(estadoPrestamoRequest)).thenReturn(estado);
        when(estadoPrestamoRepository.save(estado)).thenReturn(estado);
        when(estadoPrestamoMapper.toDto(estado)).thenReturn(estadoDto);
        EstadoPrestamoResponse result = estadoPrestamoService.createEstadoPrestamo(estadoPrestamoRequest);
        assertAll(
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Aprobado", result.nombre())
        );
        verify(estadoPrestamoMapper).toEntity(estadoPrestamoRequest);
        verify(estadoPrestamoRepository).save(estado);
        verify(estadoPrestamoMapper).toDto(estado);
    }
    @Test
    @DisplayName("updateEstadoPrestamo with existing id updates and returns EstadoPrestamoDto")
    void updateEstadoPrestamo_ExistingId_UpdatesAndReturnsEstadoPrestamoDto() {
        Long id = 1L;
        EstadoPrestamoRequest estadoPrestamoRequest = EstadoPrestamoRequest
                .builder()
                .nombre("Rechazado")
                .build();
        EstadoPrestamo estado = EstadoPrestamo.builder().id(id).nombre("Aprobado").build();
        EstadoPrestamo updatedEstado = EstadoPrestamo.builder().id(id).nombre("Rechazado").build();
        EstadoPrestamoResponse estadoDto = EstadoPrestamoResponse
                .builder()
                .id(id)
                .nombre("Rechazado")
                .build();
        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.of(estado));
        when(estadoPrestamoRepository.save(estado)).thenReturn(updatedEstado);
        when(estadoPrestamoMapper.toDto(updatedEstado)).thenReturn(estadoDto);
        EstadoPrestamoResponse result = estadoPrestamoService.updateEstadoPrestamo(id, estadoPrestamoRequest);
        assertAll(
                () -> assertEquals(id, result.id()),
                () -> assertEquals("Rechazado", result.nombre())
        );
        verify(estadoPrestamoRepository).findById(id);
        verify(estadoPrestamoRepository).save(estado);
        verify(estadoPrestamoMapper).toDto(updatedEstado);
    }
    @Test
    @DisplayName("updateEstadoPrestamo with non-existing id throws ResourceNotFound")
    void updateEstadoPrestamo_NonExistingId_ThrowsResourceNotFound() {
        Long id = 1L;
        EstadoPrestamoRequest estadoPrestamoRequest = EstadoPrestamoRequest
                .builder()
                .nombre("Rechazado")
                .build();
        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoPrestamoService.updateEstadoPrestamo(id, estadoPrestamoRequest));
        assertEquals("Estado de préstamo no encontrado con id: " + id, exception.getMessage());
        verify(estadoPrestamoRepository).findById(id);
    }
    @Test
    @DisplayName("deleteEstadoPrestamo with existing id deletes EstadoPrestamo")
    void deleteEstadoPrestamo_ExistingId_DeletesEstadoPrestamo() {
        Long id = 1L;
        EstadoPrestamo estado = EstadoPrestamo.builder().id(id).nombre("Aprobado").build();

        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.of(estado));

        estadoPrestamoService.deleteEstadoPrestamo(id);

        verify(estadoPrestamoRepository).findById(id);
        verify(estadoPrestamoRepository).delete(estado);
    }
    @Test
    @DisplayName("deleteEstadoPrestamo with non-existing id throws ResourceNotFound")
    void deleteEstadoPrestamo_NonExistingId_ThrowsResourceNotFound() {
        Long id = 1L;
        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.empty());
        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoPrestamoService.deleteEstadoPrestamo(id));
        assertEquals("Estado de préstamo no encontrado con id: " + id, exception.getMessage());
        verify(estadoPrestamoRepository).findById(id);
    }
    @Test
    @DisplayName("getEstadoPrestamoEntityById with existing id returns EstadoPrestamo entity")
    void getEstadoPrestamoEntityById() {
        Long id = 1L;
        EstadoPrestamo estado = EstadoPrestamo.builder().id(id).nombre("Aprobado").build();

        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.of(estado));

        EstadoPrestamo result = estadoPrestamoService.getEstadoPrestamoEntityById(id);

        assertAll(
                () -> assertEquals(id, result.getId()),
                () -> assertEquals("Aprobado", result.getNombre())
        );

        verify(estadoPrestamoRepository).findById(id);
    }
    @Test
    @DisplayName("getEstadoPrestamoEntityById with non-existing id throws ResourceNotFound")
    void getEstadoPrestamoEntityById_NonExistingId_ThrowsResourceNotFound() {
        Long id = 1L;
        when(estadoPrestamoRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> estadoPrestamoService.getEstadoPrestamoEntityById(id));
        assertEquals("Estado de préstamo no encontrado con id: " + id, exception.getMessage());

        verify(estadoPrestamoRepository).findById(id);
    }

}
