package com.alura.forum.forumHub.infra.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DatabaseLogService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public void log(String level, String message, String logger, Long userId, String ipAddress, String endpoint) {
        try {
            jdbcTemplate.update(
                    """
                    INSERT INTO system_logs (level, message, logger, user_id, ip_address, endpoint)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    level, message, logger, userId, ipAddress, endpoint
            );
        } catch (Exception e) {
            // Si falla, lo manda a consola
            System.err.println("Failed to log to database: " + e.getMessage());
        }
    }
}
