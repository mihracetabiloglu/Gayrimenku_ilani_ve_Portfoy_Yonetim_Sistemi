package com.gayrimenkul.system.security;

import com.gayrimenkul.system.entity.User;
import com.gayrimenkul.system.repository.UserRepository;
import com.gayrimenkul.system.service.SystemLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SystemAuditFilter extends OncePerRequestFilter {

    private static final Set<String> AUDITED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final SystemLogService systemLogService;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/logs")
                || path.startsWith("/api/admin/logs")
                || !AUDITED_METHODS.contains(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Exception failure = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            failure = e;
            throw e;
        } finally {
            writeAuditLog(request, response, failure);
        }
    }

    private void writeAuditLog(HttpServletRequest request, HttpServletResponse response, Exception failure) {
        try {
            Optional<User> currentUser = resolveCurrentUser();
            Long userId = currentUser.map(User::getId).orElse(null);
            String username = currentUser.map(User::getUsername).orElse("anonymous");
            int status = response.getStatus();
            String action = request.getMethod() + "_" + normalizePath(request.getServletPath());
            String result = failure == null && status < 400 ? "SUCCESS" : "FAIL";
            String details = String.format("%s %s %s by %s returned %d",
                    request.getMethod(), request.getServletPath(), result, username, status);
            if (failure != null) {
                details += " - " + failure.getClass().getSimpleName();
            }

            systemLogService.createLog(userId, action, details, resolveClientIp(request));
        } catch (Exception ignored) {
            // Audit loglama basarisiz olsa bile asil islem etkilenmemeli.
        }
    }

    private Optional<User> resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(authentication.getName());
    }

    private String normalizePath(String path) {
        String normalized = path == null ? "UNKNOWN" : path;
        normalized = normalized.replaceFirst("^/api/", "");
        normalized = normalized.replaceAll("[^A-Za-z0-9]+", "_");
        normalized = normalized.replaceAll("^_+|_+$", "");
        return normalized.isBlank() ? "API" : normalized.toUpperCase();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
