package com.alura.forum.forumHub.domain.topico;

import com.alura.forum.forumHub.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    @Query("SELECT COUNT(t) > 0 FROM Topico t WHERE " +
            "LOWER(TRIM(t.titulo)) = LOWER(TRIM(:titulo)) AND " +
            "LOWER(TRIM(t.mensaje)) = LOWER(TRIM(:mensaje))")
    boolean existsByTituloAndMensaje(@Param("titulo") String titulo,
                                     @Param("mensaje") String mensaje);

    @Query("SELECT COUNT(t) > 0 FROM Topico t WHERE " +
            "LOWER(TRIM(t.titulo)) = LOWER(TRIM(:titulo)) AND " +
            "LOWER(TRIM(t.mensaje)) = LOWER(TRIM(:mensaje)) AND " +
            "t.id <> :id")
    boolean existsByTituloAndMensajeAndIdNot(@Param("titulo") String titulo,
                                             @Param("mensaje") String mensaje,
                                             @Param("id") Long id);

    @EntityGraph(attributePaths = {"usuario"})
    @Query("SELECT t FROM Topico t WHERE " +
            "(:curso IS NULL OR t.curso = :curso) AND " +
            "(:estado IS NULL OR t.status = :estado) AND " +
            "(:fechaInicio IS NULL OR t.fechaCreacion >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR t.fechaCreacion <= :fechaFin)")
    Page<Topico> filtrarTopicos(
            @Param("curso") String curso,
            @Param("estado") StatusTopico estado,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {"usuario"})
    Page<Topico> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"usuario"})
    Optional<Topico> findById(Long id);

    // Consulta optimizada para búsqueda
    @Query(value = """
        SELECT t.id, t.titulo, t.mensaje, t.fecha_creacion, 
               t.status, u.login AS autor_login, t.curso,
               MATCH(t.titulo, t.mensaje) AGAINST(:query IN NATURAL LANGUAGE MODE) AS relevancia
        FROM topicos t 
        JOIN usuarios u ON t.usuario_id = u.id 
        WHERE MATCH(t.titulo, t.mensaje) AGAINST(:query IN NATURAL LANGUAGE MODE)
        ORDER BY relevancia DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> buscarTopicosOptimizado(@Param("query") String query, @Param("limit") int limit);

    // Consulta optimizada para trending
    @Query(value = """
        SELECT t.id, t.titulo, t.mensaje, t.fecha_creacion, 
               t.status, u.login AS autor_login, t.curso, 0.0 AS relevancia
        FROM topicos t 
        JOIN usuarios u ON t.usuario_id = u.id 
        LEFT JOIN respuestas r ON t.id = r.topico_id 
        WHERE t.fecha_creacion >= DATE_SUB(NOW(), INTERVAL 7 DAY) 
        GROUP BY t.id
        ORDER BY COUNT(r.id) DESC, t.fecha_creacion DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTrendingOptimizado(@Param("limit") int limit);

    // Consulta para estadísticas de cursos
    @Query("""
        SELECT t.curso as curso, 
               COUNT(t.id) as totalTopicos,
               SUM(CASE WHEN t.status = 'SOLUCIONADO' THEN 1 ELSE 0 END) as solucionados
        FROM Topico t 
        GROUP BY t.curso
        """)
    List<EstadisticasCurso> getEstadisticasPorCurso();

    @Query("SELECT COUNT(t) FROM Topico t WHERE t.usuario = :usuario AND DATE(t.fechaCreacion) = CURRENT_DATE")
    long countTopicosByUsuarioToday(@Param("usuario") Usuario usuario);

    long countByStatus(StatusTopico status);
}