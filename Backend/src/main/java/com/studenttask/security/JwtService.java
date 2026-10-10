package com.studenttask.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final String secret;
    private final long expiration;
    private SecretKey cachedKey;

    public JwtService(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    @PostConstruct
    public void init() {
        log.info("JwtService initialized with expiration: {} ms", expiration);
        this.cachedKey = getSigningKey();
    }

    private SecretKey getSigningKey() {
        if (cachedKey != null) {
            return cachedKey;
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        cachedKey = Keys.hmacShaKeyFor(secretBytes);
        return cachedKey;
    }

    public String generateToken(String email) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = getSigningKey();
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("Token expired: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.debug("Failed to parse token: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isTokenValid(String token, String userEmail) {
        final String email = extractEmail(token);
        if (email == null) {
            return false;
        }
        return email.equals(userEmail) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        if (expiration == null) {
            return true;
        }
        return expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}