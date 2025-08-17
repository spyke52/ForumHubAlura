package com.alura.forum.forumHub.domain.topico;

import java.time.LocalDateTime;

public record TopicoConAutorDTO(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        StatusTopico status,
        String autorLogin,
        String curso,
        Double relevancia
) {
    public TopicoConAutorDTO(Object[] row) {
        this(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                ((java.sql.Timestamp) row[3]).toLocalDateTime(),
                StatusTopico.valueOf((String) row[4]),
                (String) row[5],
                (String) row[6],
                row[7] != null ? ((Number) row[7]).doubleValue() : 0.0
        );
    }
}