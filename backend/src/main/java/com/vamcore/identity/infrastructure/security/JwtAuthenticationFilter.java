package com.vamcore.identity.infrastructure.security;

import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtro de seguridad que:
 *  1. Extrae y valida el JWT del header Authorization.
 *  2. Puebla el SecurityContext de Spring (para @PreAuthorize / hasRole).
 *  3. Puebla el TenantContext (ver ADR-005 - el tenant SIEMPRE sale del
 *     token autenticado, nunca de un parámetro de la request).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtTokenProvider.parseClaims(token);

                TenantContext.set(TenantId.of(jwtTokenProvider.extractTenantId(claims)));

                List<String> roles = claims.get("roles", List.class);
                List<String> permissions = claims.get("permissions", List.class);

                List<GrantedAuthority> authorities = new ArrayList<>();
                if (roles != null) {
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                }
                if (permissions != null) {
                    // Sin prefijo: se comprueban con hasAuthority('CODIGO'), no con hasRole(...).
                    permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
                }

                var authentication = new UsernamePasswordAuthenticationToken(
                    jwtTokenProvider.extractUserId(claims), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                // Token inválido/expirado: se deja sin autenticar, Spring Security
                // responderá 401/403 según la regla de la ruta.
                SecurityContextHolder.clearContext();
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
