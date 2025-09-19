package org.nttdata.com.servicioprestamos.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicioprestamos.producer.dto.ClienteResponseK;
import org.nttdata.com.servicioprestamos.producer.dto.NotificacionRequestK;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Date;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificacionProducerTest {

    @Mock
    private KafkaTemplate<String, NotificacionRequestK> kafkaTemplate;

    @InjectMocks
    private NotificacionProducer notificacionProducer;

    @Test
    @DisplayName("Debe enviar notificación a Kafka exitosamente")
    void enviarNotificacionSuccess() {
        NotificacionRequestK request = NotificacionRequestK.builder()
                .cliente(ClienteResponseK.builder()
                        .id(1L)
                        .nombre("Juan")
                        .email("juan@test.com")
                        .dni("12345678")
                        .estado("ACTIVO")
                        .build())
                .tipoNotificacionId(1L)
                .asunto("Prueba de notificación")
                .mensaje("Mensaje de prueba")
                .fechaEnvio(new Date())
                .estadoNotificacionId(1L)
                .build();

        notificacionProducer.enviarNotificacion(request);

        verify(kafkaTemplate, times(1))
                .send(eq("notificaciones"), eq(request));
    }
}
