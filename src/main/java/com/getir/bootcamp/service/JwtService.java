package com.getir.bootcamp.service;

import com.getir.bootcamp.config.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class JwtService {
    private final TokenProperties tokenProperties;

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, tokenProperties.getAccessToken().getMaxAge());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, tokenProperties.getRefreshToken().getMaxAge());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolvers) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
        return claimResolvers.apply(claims);
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(tokenProperties.getSecretKey());
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private String generateToken(UserDetails userDetails, Long maxAgeMillis) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + maxAgeMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
