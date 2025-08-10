package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.topico.*;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosDetalleTopico> registrarTopico(
            @RequestBody @Valid DatosRegistroTopico datos,
            UriComponentsBuilder uriBuilder,
            Authentication authentication) {

        var usuario = (Usuario) authentication.getPrincipal();

        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ValidacionException("Este tópico ya existe");
        }

        var topico = new Topico(datos);
        topico.setUsuario(usuario);
        topicoRepository.save(topico);

        URI url = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(new DatosDetalleTopico(topico));
    }

    @GetMapping
    public ResponseEntity<Page<DatosDetalleTopico>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion) {

        var page = topicoRepository.findAll(paginacion).map(DatosDetalleTopico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosDetalleTopico> detallarTopico(@PathVariable Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosDetalleTopico> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid DatosActualizarTopico datos,
            Authentication authentication) {

        var usuario = (Usuario) authentication.getPrincipal();

        if (!id.equals(datos.id())) {
            throw new ValidacionException("El ID del path no coincide con el cuerpo");
        }

        var topico = topicoRepository.findById(datos.id())
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        if (!topico.getUsuario().getId().equals(usuario.getId())) {
            throw new ValidacionException("No tienes permiso para modificar este tópico");
        }

        String tituloActual = datos.titulo() != null ? datos.titulo() : topico.getTitulo();
        String mensajeActual = datos.mensaje() != null ? datos.mensaje() : topico.getMensaje();

        if (topicoRepository.existsByTituloAndMensajeAndIdNot(tituloActual, mensajeActual, id)) {
            throw new ValidacionException("Este tópico ya existe");
        }

        topico.actualizarDatos(datos);
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> eliminarTopico(@PathVariable Long id, Authentication authentication) {
        var usuario = (Usuario) authentication.getPrincipal();

        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Tópico no encontrado"));

        if (!topico.getUsuario().getId().equals(usuario.getId())) {
            throw new ValidacionException("No tienes permiso para eliminar este tópico");
        }

        topicoRepository.delete(topico);
        return ResponseEntity.noContent().build();
    }
}