package com.alura.forum.forumHub.domain.notification;

import com.alura.forum.forumHub.domain.respuesta.Respuesta;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Async
    public void notificarNuevaRespuesta(Usuario autorTopico, Respuesta respuesta) {
        Notification notification = new Notification();
        notification.setUsuario(autorTopico);
        notification.setMensaje("Nueva respuesta en tu tópico: " + respuesta.getTopico().getTitulo());
        notification.setTipo(TipoNotificacion.NUEVA_RESPUESTA);
        notificationRepository.save(notification);
    }
}