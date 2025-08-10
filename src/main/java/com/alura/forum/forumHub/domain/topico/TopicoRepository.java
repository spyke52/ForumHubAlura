package com.alura.forum.forumHub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensaje(String titulo, String mensaje);
    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Topico> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"usuario"})
    Optional<Topico> findById(Long id);
}