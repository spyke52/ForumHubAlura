package com.alura.forum.forumHub.domain.topico.service;

import com.alura.forum.forumHub.domain.topico.Topico;
import com.alura.forum.forumHub.domain.topico.TopicoRepository;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicoValidator {

    private final TopicoRepository topicoRepository;

    @Autowired
    public TopicoValidator(TopicoRepository topicoRepository) {
        this.topicoRepository = topicoRepository;
    }

    public void validarCoincidenciaIds(Long pathId, Long bodyId) {
        if (!pathId.equals(bodyId)) {
            throw new ValidacionException("El ID del path no coincide con el cuerpo");
        }
    }

    public void validarPermisoModificacion(Topico topico, Usuario usuario) {
        if (!topico.getUsuario().getId().equals(usuario.getId())) {
            throw new ValidacionException("No tienes permiso para modificar este tópico");
        }
    }

    public void validarDuplicado(String titulo, String mensaje, Long excludeId) {
        if (topicoRepository.existsByTituloAndMensajeAndIdNot(titulo, mensaje, excludeId)) {
            throw new ValidacionException("Este tópico ya existe");
        }
    }

    public void validarDuplicado(String titulo, String mensaje) {
        if (topicoRepository.existsByTituloAndMensaje(titulo, mensaje)) {
            throw new ValidacionException("Este tópico ya existe");
        }
    }
}