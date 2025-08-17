package com.alura.forum.forumHub.infra.config;

import com.alura.forum.forumHub.domain.topico.DatosDetalleTopico;
import com.alura.forum.forumHub.infra.cache.SimpleCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {
    @Bean
    public SimpleCache<String, DatosDetalleTopico> topicoCache() {
        return new SimpleCache<>(30); // 30 minutos TTL
    }
}