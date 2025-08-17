package com.alura.forum.forumHub.domain.service;

import com.alura.forum.forumHub.domain.respuesta.ContribuidorTop;
import com.alura.forum.forumHub.domain.respuesta.RespuestaRepository;
import com.alura.forum.forumHub.domain.stats.DashboardStatsDTO;
import com.alura.forum.forumHub.domain.topico.EstadisticasCurso;
import com.alura.forum.forumHub.domain.topico.StatusTopico;
import com.alura.forum.forumHub.domain.topico.TopicoRepository;
import com.alura.forum.forumHub.domain.usuario.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstadisticasService {

    private final TopicoRepository topicoRepository;
    private final RespuestaRepository respuestaRepository;
    private final UsuarioRepository usuarioRepository;

    public EstadisticasService(TopicoRepository topicoRepository,
                               RespuestaRepository respuestaRepository,
                               UsuarioRepository usuarioRepository) {
        this.topicoRepository = topicoRepository;
        this.respuestaRepository = respuestaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO obtenerEstadisticasDashboard() {
        return new DashboardStatsDTO(
                topicoRepository.count(),
                topicoRepository.countByStatus(StatusTopico.SOLUCIONADO),
                respuestaRepository.count(),
                usuarioRepository.count(),
                topicoRepository.getEstadisticasPorCurso(),
                respuestaRepository.findTopContribuidores(PageRequest.of(0, 5))
        );
    }
}