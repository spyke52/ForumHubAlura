package com.alura.forum.forumHub.domain.service;

import com.alura.forum.forumHub.domain.topico.TopicoConAutorDTO;
import com.alura.forum.forumHub.domain.topico.TopicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaService {

    private final TopicoRepository topicoRepository;

    public BusquedaService(TopicoRepository topicoRepository) {
        this.topicoRepository = topicoRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicoConAutorDTO> buscarTopicos(String query, int limit) {
        List<Object[]> resultados = topicoRepository.buscarTopicosOptimizado(query, limit);
        return resultados.stream()
                .map(TopicoConAutorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicoConAutorDTO> obtenerTrending(int limit) {
        List<Object[]> resultados = topicoRepository.findTrendingOptimizado(limit);
        return resultados.stream()
                .map(TopicoConAutorDTO::new)
                .collect(Collectors.toList());
    }
}