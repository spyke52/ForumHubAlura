package com.alura.forum.forumHub.domain.respuesta;

import jakarta.validation.constraints.NotBlank;

public record DatosRegistroRespuesta(
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje
) {}