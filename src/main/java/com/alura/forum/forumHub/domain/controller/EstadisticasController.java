package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.respuesta.ContribuidorTop;
import com.alura.forum.forumHub.domain.service.BusquedaService;
import com.alura.forum.forumHub.domain.service.EstadisticasService;
import com.alura.forum.forumHub.domain.stats.DashboardStatsDTO;
import com.alura.forum.forumHub.domain.topico.EstadisticasCurso;
import com.alura.forum.forumHub.domain.topico.TopicoConAutorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = "bearer-key")
public class EstadisticasController {

    private final BusquedaService busquedaService;
    private final EstadisticasService estadisticasService;

    public EstadisticasController(BusquedaService busquedaService,
                                  EstadisticasService estadisticasService) {
        this.busquedaService = busquedaService;
        this.estadisticasService = estadisticasService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Estadísticas del dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticasDashboard());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<TopicoConAutorDTO>> getTrendingTopics() {
        return ResponseEntity.ok(busquedaService.obtenerTrending(10));
    }

    @Data
    @Builder
    static class DashboardStats {
        private long totalTopicos;
        private long topicosSolucionados;
        private long totalRespuestas;
        private long totalUsuarios;
        private List<EstadisticasCurso> topicosPorCurso;
        private List<ContribuidorTop> topContribuidores;
    }
}