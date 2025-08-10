package com.alura.forum.forumHub.infra.security;

import com.alura.forum.forumHub.domain.usuario.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AutenticacionService autenticacionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            // Manejar "Bearer" en cualquier combinación de mayúsculas/minúsculas
            String token = authHeader.replaceAll("(?i)bearer ", "").trim();

            if (!token.isEmpty()) {
                try {
                    var subject = tokenService.getSubject(token);
                    if (subject != null) {
                        var usuario = (Usuario) autenticacionService.loadUserByUsername(subject);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (RuntimeException e) {
                    // Registrar error y responder con 401
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}