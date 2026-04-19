package com.hospitalappointment.security;

import com.hospitalappointment.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        if (jwtProperties.secret() == null || jwtProperties.secret().isBlank() || jwtProperties.secret().length() < 32) {
            throw new IllegalStateException("JWT secret must be configured and contain at least 32 characters");
        }
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.accessTokenExpirationMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of("roles", user.getRoles().stream().map(role -> role.getName().name()).toList()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtProperties.refreshTokenExpirationDays(), ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long accessTokenExpirySeconds() {
        return jwtProperties.accessTokenExpirationMinutes() * 60;
    }

    public long refreshTokenExpiryDays() {
        return jwtProperties.refreshTokenExpirationDays();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
