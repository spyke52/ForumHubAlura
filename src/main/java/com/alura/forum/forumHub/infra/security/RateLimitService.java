package com.alura.forum.forumHub.infra.security;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
    private final JdbcTemplate jdbcTemplate;

    public RateLimitService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isAllowed(String ipAddress, String endpoint, int maxRequests, int windowMinutes) {
        try {
            // Limpiar registros antiguos
            jdbcTemplate.update(
                    "DELETE FROM rate_limits WHERE window_start < DATE_SUB(NOW(), INTERVAL ? MINUTE)",
                    windowMinutes
            );

            // Verificar límite actual - Manejar excepción cuando no hay resultados
            Integer count = null;
            try {
                count = jdbcTemplate.queryForObject(
                        """
                        SELECT request_count FROM rate_limits
                        WHERE ip_address = ? AND endpoint = ?
                        AND window_start > DATE_SUB(NOW(), INTERVAL ? MINUTE)
                        """,
                        Integer.class, ipAddress, endpoint, windowMinutes
                );
            } catch (EmptyResultDataAccessException e) {
                // No hay registros previos, esto es normal
                count = null;
            }

            if (count == null) {
                // Primera request en esta ventana
                jdbcTemplate.update(
                        "INSERT INTO rate_limits (ip_address, endpoint, request_count) VALUES (?, ?, 1)",
                        ipAddress, endpoint
                );
                return true;
            } else if (count < maxRequests) {
                // Incrementar contador
                jdbcTemplate.update(
                        """
                        UPDATE rate_limits
                        SET request_count = request_count + 1
                        WHERE ip_address = ? AND endpoint = ?
                        """,
                        ipAddress, endpoint
                );
                return true;
            }

            return false; // Límite excedido
        } catch (Exception e) {
            // En caso de error con la base de datos, permitir la request para no bloquear el sistema
            System.err.println("Error en rate limiting: " + e.getMessage());
            return true;
        }
    }
}