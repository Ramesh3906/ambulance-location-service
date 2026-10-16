package com.ambulance.security;

import com.ambulance.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey key;
    private final SecureRandom random = new SecureRandom();

    public JwtService(JwtProperties properties) {
        this.properties = properties;

        if (properties.getSecret() == null || properties.getSecret().isBlank()) {
            throw new IllegalStateException("ambulance.security.jwt.secret must be configured");
        }

        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret must decode to at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long driverId, String email, Long ambulanceId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(properties.getAccessTokenMinutes()));

        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(driverId))
                .claim("email", email)
                .claim("ambulanceId", ambulanceId)
                .claim("role", "DRIVER")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Invalid or expired access token");
        }
    }

    public long accessTokenSeconds() {
        return properties.getAccessTokenMinutes() * 60;
    }

    /** Opaque refresh token; only its SHA-256 hash is persisted. */
    public String generateRefreshToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashRefreshToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(token.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash refresh token", e);
        }
    }

    public Instant refreshTokenExpiry() {
        return Instant.now().plus(Duration.ofDays(properties.getRefreshTokenDays()));
    }
}