package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.usuario.DatosDetalleUsuario;
import com.alura.forum.forumHub.domain.usuario.DatosRegistroUsuario;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.domain.usuario.UsuarioRepository;
import com.alura.forum.forumHub.infra.exceptions.ValidacionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<DatosDetalleUsuario> registrarUsuario(
            @RequestBody @Valid DatosRegistroUsuario datos,
            UriComponentsBuilder uriBuilder) {

        if (usuarioRepository.existsByLogin(datos.login())) {
            throw new ValidacionException("El usuario ya existe");
        }

        var usuario = new Usuario();
        usuario.setLogin(datos.login());
        usuario.setClave(passwordEncoder.encode(datos.clave()));
        usuarioRepository.save(usuario);

        URI url = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(url).body(new DatosDetalleUsuario(usuario));
    }

    // Metodo temporal para verificar la contraseñas
    @GetMapping("/verify-password")
    public ResponseEntity<String> verifyPassword(
            @RequestParam String rawPassword,
            @RequestParam String encodedPassword) {

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        return ResponseEntity.ok("Coincide: " + matches);
    }
}