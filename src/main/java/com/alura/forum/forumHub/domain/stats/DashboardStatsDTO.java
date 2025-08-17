package com.alura.forum.forumHub.domain.stats;

import com.alura.forum.forumHub.domain.respuesta.ContribuidorTop;
import com.alura.forum.forumHub.domain.topico.EstadisticasCurso;

import java.util.List;

public record DashboardStatsDTO(
        long totalTopicos,
        long topicosSolucionados,
        long totalRespuestas,
        long totalUsuarios,
        List<EstadisticasCurso> estadisticasPorCurso,
        List<ContribuidorTop> topContribuyentes
) {}