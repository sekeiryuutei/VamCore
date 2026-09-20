package com.vamcore.identity.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vamcore.shared.infrastructure.exception.ApiError;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @EnableMethodSecurity activa @PreAuthorize en los controladores (ver
 * sección 32 del documento de arquitectura - permisos granulares). Las
 * rutas siguen protegidas también a nivel de filterChain (autenticación
 * JWT obligatoria salvo las explícitamente públicas); @PreAuthorize añade
 * una segunda capa de autorización POR PERMISO, no solo por estar logueado.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas: registro/login y utilidades de plataforma.
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/tenants").permitAll() // alta inicial de empresa
                .requestMatchers("/actuator/health/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptions -> exceptions
                // Sin este handler, un @PreAuthorize que rechaza devuelve un 403
                // vacío (sin JSON) porque Spring Security responde ANTES de
                // llegar a GlobalExceptionHandler. Con esto, el frontend
                // siempre recibe el mismo formato ApiError, venga de donde venga.
                .accessDeniedHandler((request, response, ex) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    String traceId = String.valueOf(request.getAttribute("correlationId"));
                    ApiError body = ApiError.of(
                        HttpStatus.FORBIDDEN.value(),
                        "FORBIDDEN_INSUFFICIENT_PERMISSION",
                        "No tienes permiso para realizar esta acción. Si acabas de actualizar el sistema, cierra sesión y vuelve a iniciarla.",
                        traceId
                    );
                    objectMapper.writeValue(response.getWriter(), body);
                })
                // Mismo problema para requests sin token válido: sin esto, un
                // 401 también sale vacío.
                .authenticationEntryPoint((request, response, ex) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    String traceId = String.valueOf(request.getAttribute("correlationId"));
                    ApiError body = ApiError.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "UNAUTHENTICATED",
                        "Tu sesión no es válida o expiró. Inicia sesión de nuevo.",
                        traceId
                    );
                    objectMapper.writeValue(response.getWriter(), body);
                })
            );

        return http.build();
    }
}
