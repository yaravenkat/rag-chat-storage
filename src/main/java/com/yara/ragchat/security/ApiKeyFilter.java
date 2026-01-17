package com.yara.ragchat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-KEY";

    @Value("${APP_SECURITY_API_KEY}")
    private String expectedApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return request.getMethod().equalsIgnoreCase("OPTIONS")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator/health");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader(HEADER_NAME);

        if (apiKey == null || !apiKey.equalsIgnoreCase(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "Unauthorized",
                  "message": "Missing or invalid API key"
                }
                """);
            return;
        }

        // Mark request as authenticated
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "api-key-user",
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
