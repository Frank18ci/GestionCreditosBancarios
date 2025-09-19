package org.nttdata.com.servicionotificaciones.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nttdata.com.servicionotificaciones.model.Notificacion;
import org.nttdata.com.servicionotificaciones.repository.NotificacionRepository;
import org.nttdata.com.servicionotificaciones.service.impl.NotificacionServiceImpl;
import org.nttdata.com.servicionotificaciones.util.NotificacionMapper;

import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {
    @InjectMocks
    private NotificacionServiceImpl notificacionService;
    @Mock
    private NotificacionRepository notificacionRepository;
    @Mock
    private NotificacionMapper notificacionMapper;



}
