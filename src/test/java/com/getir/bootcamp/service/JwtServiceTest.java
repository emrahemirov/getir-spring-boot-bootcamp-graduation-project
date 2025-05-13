package com.getir.bootcamp.service;

import com.getir.bootcamp.config.TokenProperties;
import com.getir.bootcamp.config.TokenProperties.AccessTokenConfig;
import com.getir.bootcamp.config.TokenProperties.RefreshTokenConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private final String base64Secret = java.util.Base64.getEncoder().encodeToString("my-super-secret-key-which-is-very-secure".getBytes(StandardCharsets.UTF_8));
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        TokenProperties tokenProperties = new TokenProperties();

        AccessTokenConfig accessTokenConfig = new AccessTokenConfig();
        accessTokenConfig.setMaxAge(1000L * 60 * 60);
        accessTokenConfig.setSecretKey(base64Secret);

        RefreshTokenConfig refreshTokenConfig = new RefreshTokenConfig();
        refreshTokenConfig.setMaxAge(1000L * 60 * 60 * 24 * 7);
        refreshTokenConfig.setCookieName("refreshToken");

        tokenProperties.setAccessToken(accessTokenConfig);
        tokenProperties.setRefreshToken(refreshTokenConfig);
        tokenProperties.setSecretKey(base64Secret);

        jwtService = new JwtService(tokenProperties);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john_doe");
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        String token = jwtService.generateAccessToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("john_doe", extractedUsername);
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        String token = jwtService.generateRefreshToken(userDetails);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("john_doe", extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateAccessToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String token = jwtService.generateAccessToken(userDetails);

        UserDetails anotherUser = mock(UserDetails.class);
        when(anotherUser.getUsername()).thenReturn("jane_doe");

        boolean isValid = jwtService.isTokenValid(token, anotherUser);
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("john_doe")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, userDetails));
    }

    private Key getSignKey() {
        byte[] key = java.util.Base64.getDecoder().decode(base64Secret);
        return Keys.hmacShaKeyFor(key);
    }
}
