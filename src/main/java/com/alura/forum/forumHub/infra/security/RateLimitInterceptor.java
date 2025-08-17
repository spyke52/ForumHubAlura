package com.alura.forum.forumHub.infra.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    public RateLimitInterceptor(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        if (!rateLimitService.isAllowed(ipAddress, endpoint, 100, 1)) { // 100 requests por minuto
            response.sendError(429, "Too many requests");
            return false;
        }

        return true;
    }
}