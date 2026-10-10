package com.studenttask.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private String testSecret = "test_jwt_secret_key_for_testing_only_minimum_256_bits_long_enough";
    private long testExpiration = 86400000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(testSecret, testExpiration);
    }

    @Test
    void generateToken_ValidEmail_ReturnsToken() {
        String token = jwtService.generateToken("test@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void extractEmail_ValidToken_ReturnsEmail() {
        String token = jwtService.generateToken("test@example.com");
        String email = jwtService.extractEmail(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void extractEmail_InvalidToken_ThrowsException() {
        assertThrows(Exception.class, () -> jwtService.extractEmail("invalid.token.here"));
    }

    @Test
    void isTokenValid_ValidTokenAndEmail_ReturnsTrue() {
        String token = jwtService.generateToken("test@example.com");

        boolean isValid = jwtService.isTokenValid(token, "test@example.com");

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WrongEmail_ReturnsFalse() {
        String token = jwtService.generateToken("test@example.com");

        boolean isValid = jwtService.isTokenValid(token, "wrong@example.com");

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_ReturnsFalse() {
        // Use a short but reasonable expiration (100ms) to allow token generation
        JwtService expiredJwtService = new JwtService(testSecret, 100);
        String token = expiredJwtService.generateToken("test@example.com");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = expiredJwtService.isTokenValid(token, "test@example.com");
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        String token = jwtService.generateToken("test@example.com");

        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ExpiredToken_ReturnsTrue() {
        // Use a short but reasonable expiration (100ms) to allow token generation
        JwtService expiredJwtService = new JwtService(testSecret, 100);
        String token = expiredJwtService.generateToken("test@example.com");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isExpired = expiredJwtService.isTokenExpired(token);
        assertTrue(isExpired);
    }

    @Test
    void generateToken_DifferentEmails_ProduceDifferentTokens() {
        String token1 = jwtService.generateToken("user1@example.com");
        String token2 = jwtService.generateToken("user2@example.com");

        assertNotEquals(token1, token2);
    }

    @Test
    void extractClaim_ValidToken_ReturnsCorrectClaim() {
        String token = jwtService.generateToken("test@example.com");
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(testSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("test@example.com", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}