package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateToken(User user, long tokenExpiration) {
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public String generateAccessToken(User user) {
        final long tokenExpiration = jwtConfig.getAccessTokenExpiration();
        return generateToken(user, tokenExpiration);
    }

    public String generateRefreshToken(User user) {
        final long tokenExpiration = jwtConfig.getRefreshTokenExpiration();
        return generateToken(user, tokenExpiration);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        }
        catch (JwtException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaimsFromToken(token).getSubject());
    }
}
