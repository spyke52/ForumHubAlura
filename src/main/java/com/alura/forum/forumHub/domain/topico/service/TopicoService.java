package com.alura.forum.forumHub.domain.topico.service;

import com.alura.forum.forumHub.domain.topico.DatosActualizarTopico;
import com.alura.forum.forumHub.domain.topico.DatosDetalleTopico;
import com.alura.forum.forumHub.domain.topico.Topico;
import com.alura.forum.forumHub.domain.topico.TopicoRepository;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.cache.SimpleCache;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TopicoService {

    private static final Logger logger = LoggerFactory.getLogger(TopicoService.class);

    private final TopicoRepository topicoRepository;
    private final SimpleCache<String, DatosDetalleTopico> cache;
    private final TopicoValidator topicoValidator;

    @Autowired
    public TopicoService(TopicoRepository topicoRepository,
                         SimpleCache<String, DatosDetalleTopico> cache,
                         TopicoValidator topicoValidator) {
        this.topicoRepository = topicoRepository;
        this.cache = cache;
        this.topicoValidator = topicoValidator;
    }

    @Transactional(readOnly = true)
    public DatosDetalleTopico obtenerTopico(Long id) {
        logger.debug("Obteniendo tópico con ID: {}", id);

        String cacheKey = "topico:" + id;

        // Intentar obtener del cache primero
        DatosDetalleTopico cached = cache.get(cacheKey);
        if (cached != null) {
            logger.debug("Tópico obtenido desde cache: {}", id);
            return cached;
        }

        // Si no está en cache, obtener de BD
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        DatosDetalleTopico datos = new DatosDetalleTopico(topico);

        // Guardar en cache
        cache.put(cacheKey, datos);
        logger.debug("Tópico guardado en cache: {}", id);

        return datos;
    }

    @Transactional(rollbackFor = Exception.class)
    public DatosDetalleTopico actualizarTopico(Long id, DatosActualizarTopico datos, Usuario usuario) {
        logger.debug("Actualizando tópico ID: {} por usuario: {}", id, usuario.getLogin());

        // Validar que los IDs coincidan
        topicoValidator.validarCoincidenciaIds(id, datos.id());

        Topico topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        // Validar permisos
        topicoValidator.validarPermisoModificacion(topico, usuario);

        // Preparar datos para validación de duplicados
        String tituloActual = datos.titulo() != null ? datos.titulo() : topico.getTitulo();
        String mensajeActual = datos.mensaje() != null ? datos.mensaje() : topico.getMensaje();

        // Validar duplicados
        topicoValidator.validarDuplicado(tituloActual, mensajeActual, id);

        try {
            // Actualizar el tópico
            topico.actualizarDatos(datos.titulo(), datos.mensaje(), datos.status());
            Topico topicoActualizado = topicoRepository.save(topico);

            // Invalidar cache
            String cacheKey = "topico:" + id;
            cache.remove(cacheKey);
            logger.debug("Cache invalidado para tópico: {}", id);

            DatosDetalleTopico resultado = new DatosDetalleTopico(topicoActualizado);

            // Actualizar cache con nuevos datos
            cache.put(cacheKey, resultado);

            return resultado;

        } catch (Exception e) {
            logger.error("Error actualizando tópico ID: {}", id, e);
            throw new ValidacionException("Error al actualizar el tópico: " + e.getMessage());
        }
    }

    public void invalidarCache(Long id) {
        String cacheKey = "topico:" + id;
        cache.remove(cacheKey);
        logger.debug("Cache invalidado manualmente para tópico: {}", id);
    }
}