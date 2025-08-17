package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.usuario.*;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Gestión de Usuarios", description = "Operaciones para administración de usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    public ResponseEntity<DatosDetalleUsuario> registrarUsuario(
            @RequestBody @Valid DatosRegistroUsuario datos,
            UriComponentsBuilder uriBuilder) {

        validarFortalezaContrasena(datos.clave());

        if (usuarioRepository.existsByLogin(datos.login())) {
            throw new ValidacionException("El usuario ya existe");
        }

        var usuario = new Usuario();
        usuario.setLogin(datos.login());
        usuario.setClave(passwordEncoder.encode(datos.clave()));
        usuario.setRol(Rol.USUARIO);
        usuarioRepository.save(usuario);

        URI url = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(url).body(new DatosDetalleUsuario(usuario));
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar rol de usuario", description = "Actualiza el rol de un usuario (solo ADMIN)")
    public ResponseEntity<DatosDetalleUsuario> actualizarRolUsuario(
            @PathVariable Long id,
            @RequestBody @Valid DatosActualizarRol datos) {

        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ValidacionException("Usuario no encontrado"));

        usuario.setRol(datos.rol());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(new DatosDetalleUsuario(usuario));
    }

    private void validarFortalezaContrasena(String clave) {
        if (clave == null || clave.length() < 8) {
            throw new ValidacionException("La contraseña debe tener mínimo 8 caracteres");
        }
        if (!clave.matches(".*[A-Z].*")) {
            throw new ValidacionException("La contraseña debe contener mayúsculas");
        }
        if (!clave.matches(".*\\d.*")) {
            throw new ValidacionException("La contraseña debe contener números");
        }
        if (!clave.matches(".*[!@#$%^&*()\\-+].*")) {
            throw new ValidacionException("La contraseña debe contener al menos un carácter especial");
        }
    }
}