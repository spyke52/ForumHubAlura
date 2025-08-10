package com.alura.forum.forumHub.domain.topico;

import com.alura.forum.forumHub.domain.usuario.Usuario;

import java.time.LocalDateTime;

public record TopicoResponse(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        String status,
        String curso,
        String autor) {

    public TopicoResponse(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFechaCreacion(),
                topico.getStatus(),
                topico.getCurso(),
                topico.getUsuario().getLogin()
        );
    }
}