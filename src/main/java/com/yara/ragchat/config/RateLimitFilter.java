package com.yara.ragchat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;
    private final int rateLimitPerMin;

    public RateLimitFilter(StringRedisTemplate redis, @Value("${RATE_LIMIT_PER_MIN:60}") int rateLimitPerMin) {
        this.redis = redis;
        this.rateLimitPerMin = rateLimitPerMin;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");
        if (apiKey == null || apiKey.isBlank()) {
            // Let ApiKeyFilter handle missing/invalid key; continue so unauthorized will be returned by ApiKeyFilter.
            filterChain.doFilter(request, response);
            return;
        }

        // Fixed-window per minute. key includes minute to separate windows.
        String minute = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
        String key = String.format("rate:%s:%s", apiKey, minute);

        Long current = redis.opsForValue().increment(key);
        if (current != null && current == 1L) {
            // first time this key is set in this window -> set TTL to 60 seconds
            redis.expire(key, Duration.ofMinutes(1));
        }

        long allowed = this.rateLimitPerMin;
        if (current != null && current <= allowed) {
            long remaining = allowed - current;
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(allowed));
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, remaining)));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too Many Requests\"}");
        }
    }
}
