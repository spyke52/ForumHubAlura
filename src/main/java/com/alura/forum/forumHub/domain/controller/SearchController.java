package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.service.BusquedaService;
import com.alura.forum.forumHub.domain.topico.TopicoConAutorDTO;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@SecurityRequirement(name = "bearer-key")
public class SearchController {

    private final BusquedaService busquedaService;

    public SearchController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping
    @Operation(summary = "Búsqueda full-text")
    public ResponseEntity<List<TopicoConAutorDTO>> buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {

        if (q.trim().length() < 3) {
            throw new ValidacionException("La búsqueda debe tener al menos 3 caracteres");
        }

        return ResponseEntity.ok(busquedaService.buscarTopicos(q, limit));
    }
}