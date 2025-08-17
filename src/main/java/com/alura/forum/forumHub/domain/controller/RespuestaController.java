package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.notification.NotificationService;
import com.alura.forum.forumHub.domain.respuesta.DatosDetalleRespuesta;
import com.alura.forum.forumHub.domain.respuesta.DatosRegistroRespuesta;
import com.alura.forum.forumHub.domain.respuesta.Respuesta;
import com.alura.forum.forumHub.domain.respuesta.RespuestaRepository;
import com.alura.forum.forumHub.domain.topico.StatusTopico;
import com.alura.forum.forumHub.domain.topico.Topico;
import com.alura.forum.forumHub.domain.topico.TopicoRepository;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos/{topicoId}/respuestas")
@SecurityRequirement(name = "bearer-key")
public class RespuestaController {

    private final RespuestaRepository respuestaRepository;
    private final TopicoRepository topicoRepository;
    private final NotificationService notificationService;

    public RespuestaController(RespuestaRepository respuestaRepository,
                               TopicoRepository topicoRepository,
                               NotificationService notificationService) {
        this.respuestaRepository = respuestaRepository;
        this.topicoRepository = topicoRepository;
        this.notificationService = notificationService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Agregar respuesta", description = "Añade una respuesta a un tópico")
    public ResponseEntity<DatosDetalleRespuesta> responder(
            @PathVariable Long topicoId,
            @RequestBody @Valid DatosRegistroRespuesta datos,
            Authentication authentication,
            UriComponentsBuilder uriBuilder) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        Respuesta respuesta = new Respuesta(datos.mensaje(), topico, usuario);
        respuestaRepository.save(respuesta);
        topico.agregarRespuesta(respuesta);

        // Notificar al autor del tópico
        notificationService.notificarNuevaRespuesta(topico.getUsuario(), respuesta);

        URI url = uriBuilder.path("/topicos/{topicoId}/respuestas/{id}")
                .buildAndExpand(topicoId, respuesta.getId()).toUri();
        return ResponseEntity.created(url).body(new DatosDetalleRespuesta(respuesta));
    }

    @PutMapping("/{respuestaId}/solucion")
    @Transactional
    @Operation(summary = "Marcar como solución", description = "Marca una respuesta como solución al tópico")
    public ResponseEntity<DatosDetalleRespuesta> marcarComoSolucion(
            @PathVariable Long topicoId,
            @PathVariable Long respuestaId,
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        if (!usuario.getId().equals(topico.getUsuario().getId())) {
            throw new ValidacionException("Solo el autor del tópico puede marcar soluciones");
        }

        Respuesta respuesta = respuestaRepository.findById(respuestaId)
                .orElseThrow(() -> new ValidacionException("Respuesta no encontrada"));

        if (!respuesta.getTopico().getId().equals(topicoId)) {
            throw new ValidacionException("La respuesta no pertenece a este tópico");
        }

        topico.marcarSolucionada(respuesta);
        return ResponseEntity.ok(new DatosDetalleRespuesta(respuesta));
    }
}