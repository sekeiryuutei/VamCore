package com.vamcore.identity.infrastructure.security;

import com.vamcore.identity.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Emite y valida los JWT usados para autenticación.
 *
 * El tenantId SIEMPRE viaja dentro del token (claim "tid") y se usa para
 * poblar el TenantContext en cada request (ver ADR-005 - Multi-tenancy).
 * Nunca se debe confiar en un tenantId enviado por query param o body.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long expirationMillis;
    private final String issuer;

    public JwtTokenProvider(
        @Value("${vamcore.security.jwt.secret}") String secret,
        @Value("${vamcore.security.jwt.expiration-minutes:60}") long expirationMinutes,
        @Value("${vamcore.platform.name:VamCore}") String issuer
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMinutes * 60 * 1000;
        this.issuer = issuer;
    }

    public String generateToken(User user, Set<String> permissions) {
        Instant now = Instant.now();
        return Jwts.builder()
            .issuer(issuer)
            .subject(user.id().toString())
            .claim("tid", user.tenantId().toString())
            .claim("email", user.email())
            .claim("roles", user.roles())
            .claim("permissions", permissions)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public UUID extractTenantId(Claims claims) {
        return UUID.fromString(claims.get("tid", String.class));
    }
}
