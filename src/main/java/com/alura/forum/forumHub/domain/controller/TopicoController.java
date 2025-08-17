package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.topico.*;
import com.alura.forum.forumHub.domain.topico.service.TopicoService;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/topicos")
@SecurityRequirement(name = "bearer-key")
public class TopicoController {

    private final TopicoRepository topicoRepository;
    private final TopicoService topicoService;

    public TopicoController(TopicoRepository topicoRepository,
                            TopicoService topicoService) {
        this.topicoRepository = topicoRepository;
        this.topicoService = topicoService;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar nuevo tópico", description = "Crea un nuevo tópico en el foro")
    public ResponseEntity<DatosDetalleTopico> registrarTopico(
            @RequestBody @Valid DatosRegistroTopico datos,
            UriComponentsBuilder uriBuilder,
            Authentication authentication) {

        var usuario = (Usuario) authentication.getPrincipal();

        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new ValidacionException("Este tópico ya existe");
        }

        var topico = new Topico(datos.titulo(), datos.mensaje(), datos.curso());
        topico.setUsuario(usuario);
        topicoRepository.save(topico);

        URI url = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(url).body(new DatosDetalleTopico(topico));
    }

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Listar tópicos", description = "Obtiene todos los tópicos paginados")
    public ResponseEntity<Page<DatosDetalleTopico>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion) {

        var page = topicoRepository.findAll(paginacion).map(DatosDetalleTopico::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/buscar")
    @Transactional(readOnly = true)
    @Operation(summary = "Buscar tópicos", description = "Búsqueda avanzada con filtros")
    public ResponseEntity<Page<DatosDetalleTopico>> buscarTopicos(
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) StatusTopico estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable paginacion) {

        var page = topicoRepository.filtrarTopicos(
                curso,
                estado,
                fechaInicio,
                fechaFin,
                paginacion
        ).map(DatosDetalleTopico::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener tópico por ID", description = "Devuelve los detalles de un tópico específico")
    public ResponseEntity<DatosDetalleTopico> detallarTopico(@PathVariable Long id) {
        return ResponseEntity.ok(topicoService.obtenerTopico(id));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualizar tópico", description = "Actualiza un tópico existente")
    public ResponseEntity<DatosDetalleTopico> actualizarTopico(
            @PathVariable Long id,
            @RequestBody @Valid DatosActualizarTopico datos,
            Authentication authentication) {

        var usuario = (Usuario) authentication.getPrincipal();

        if (!id.equals(datos.id())) {
            throw new ValidacionException("El ID del path no coincide con el cuerpo");
        }

        return ResponseEntity.ok(topicoService.actualizarTopico(id, datos, usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Eliminar tópico", description = "Elimina un tópico existente")
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