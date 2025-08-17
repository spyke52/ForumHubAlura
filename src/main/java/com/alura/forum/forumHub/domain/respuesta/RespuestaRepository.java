package com.alura.forum.forumHub.domain.respuesta;

import com.alura.forum.forumHub.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    Page<Respuesta> findByTopicoIdOrderByFechaCreacionAsc(Long topicoId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Respuesta r WHERE r.autor = :autor")
    long countByAutor(@Param("autor") Usuario autor);

    @Query("""
        SELECT r FROM Respuesta r 
        JOIN FETCH r.topico t 
        WHERE r.autor = :autor 
        ORDER BY r.fechaCreacion DESC
        """)
    List<Respuesta> findRecentByAutor(@Param("autor") Usuario autor, Pageable pageable);

    @Query("""
        SELECT r.autor.login as autor, COUNT(r.id) as solucionesCount
        FROM Respuesta r 
        WHERE r.solucion = true 
        GROUP BY r.autor.id 
        ORDER BY solucionesCount DESC
        """)
    List<ContribuidorTop> findTopContribuidores(Pageable pageable);
}