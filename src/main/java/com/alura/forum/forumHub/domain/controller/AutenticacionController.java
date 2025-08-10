package com.alura.forum.forumHub.domain.controller;

import com.alura.forum.forumHub.domain.usuario.DatosAutenticacionUsuario;
import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.alura.forum.forumHub.infra.security.DatosJWTToken;
import com.alura.forum.forumHub.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<DatosJWTToken> autenticarUsuario(@RequestBody DatosAutenticacionUsuario datos) {
        var authToken = new UsernamePasswordAuthenticationToken(
                datos.login(),
                datos.clave());

        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }
}