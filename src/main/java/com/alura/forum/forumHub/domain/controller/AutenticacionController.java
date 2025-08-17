package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.usuario.DatosAutenticacionUsuario;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.security.DatosJWTToken;
import com.alura.forum.forumHub.infra.security.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacionController(AuthenticationManager authenticationManager,
                                   TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<DatosJWTToken> autenticarUsuario(
            @RequestBody DatosAutenticacionUsuario datos) {

        var authToken = new UsernamePasswordAuthenticationToken(
                datos.login(), datos.clave());

        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var jwtToken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(new DatosJWTToken(jwtToken));
    }
}