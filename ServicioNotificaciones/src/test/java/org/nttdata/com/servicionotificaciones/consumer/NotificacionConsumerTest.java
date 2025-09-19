package org.nttdata.com.servicionotificaciones.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.consumer.dto.ClienteResponseK;
import org.nttdata.com.servicionotificaciones.consumer.dto.NotificacionRequestK;
import org.nttdata.com.servicionotificaciones.dto.EstadoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.NotificacionRequest;
import org.nttdata.com.servicionotificaciones.dto.NotificacionResponse;
import org.nttdata.com.servicionotificaciones.dto.TipoNotificacionResponse;
import org.nttdata.com.servicionotificaciones.model.TipoNotificacion;
import org.nttdata.com.servicionotificaciones.service.MailManagerPersonalizado;
import org.nttdata.com.servicionotificaciones.service.NotificacionService;

import java.time.LocalDateTime;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NotificacionConsumerTest {
    @Mock
    private NotificacionService notificacionService;
    @Mock
    private MailManagerPersonalizado mailManager;
    @InjectMocks
    private NotificacionConsumer notificacionConsumer;

    @Test
    @DisplayName("consumirNotificacion should create notification and send email when message is valid")
    void consumirNotificacionCreatesNotificationAndSendsEmail() {
        NotificacionRequestK notificacionRequestK = NotificacionRequestK
                        .builder()
                        .cliente(ClienteResponseK.builder()
                            .id(1L)
                            .dni("12345678")
                            .nombre("Juan Perez")
                            .email("juanperez@gmail.com")
                            .estado("ACTIVO")
                            .build())
                        .estadoNotificacionId(1L)
                        .tipoNotificacionId(1L)
                        .asunto("Asunto")
                        .mensaje("Mensaje")
                        .fechaEnvio(new Date())
                        .build();
        NotificacionRequest mensaje = NotificacionRequest
                .builder()
                .clienteId(notificacionRequestK.cliente().id())
                .asunto(notificacionRequestK.asunto())
                .mensaje(notificacionRequestK.mensaje())
                .estadoNotificacionId(notificacionRequestK.estadoNotificacionId())
                .tipoNotificacionId(notificacionRequestK.tipoNotificacionId())
                .fechaEnvio(new Date())
                .build();
        NotificacionResponse cliente = NotificacionResponse.builder()
                .id(1L)
                .clienteId(1L)
                .asunto("Asunto")
                .mensaje("Mensaje")
                .estadoNotificacion(EstadoNotificacionResponse.builder().id(1L).estado("ENVIADO").build())
                .tipoNotificacion(TipoNotificacionResponse.builder().id(1L).nombre("EMAIL").build())
                .fechaEnvio(new Date())
                .build();

        when(notificacionService.createNotificacion(any(NotificacionRequest.class))).thenReturn(cliente);
        notificacionConsumer.consumirNotificacion(notificacionRequestK);

        ArgumentCaptor<NotificacionRequest> captor = ArgumentCaptor.forClass(NotificacionRequest.class);
        verify(notificacionService).createNotificacion(captor.capture());


        assertEquals("El id del cliente no coincide", notificacionRequestK.cliente().id(), captor.getValue().clienteId());
        assertEquals("El asunto no coincide", mensaje.asunto(), captor.getValue().asunto());
        assertEquals("El mensaje no coincide", mensaje.mensaje(), captor.getValue().mensaje());

        verify(mailManager).sendMessage(
                eq(notificacionRequestK.cliente().email()),
                eq(mensaje.asunto()),
                anyString()
        );
    }
}
