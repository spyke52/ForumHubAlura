package com.alura.forum.forumHub.domain.respuesta;

import java.time.LocalDateTime;

public record DatosDetalleRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        boolean solucion,
        String autor,
        Long topicoId
) {
    public DatosDetalleRespuesta(Respuesta respuesta) {
        this(
                respuesta.getId(),
                respuesta.getMensaje(),
                respuesta.getFechaCreacion(),
                respuesta.isSolucion(),
                respuesta.getAutor().getLogin(),
                respuesta.getTopico().getId()
        );
    }
}