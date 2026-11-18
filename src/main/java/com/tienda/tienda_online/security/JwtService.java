package com.tienda.tienda_online.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, jwtExpiration, "access");
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshExpiration, "refresh");
    }

    private String generateToken(
            UserDetails userDetails,
            long expiration,
            String tokenType) {

        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(
                fechaActual.getTime() + expiration
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("tokenType", tokenType)
                .issuedAt(fechaActual)
                .expiration(fechaExpiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject().equals(userDetails.getUsername())
                    && "access".equals(claims.get("tokenType", String.class))
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Validar refresh token
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {

        try {
            Claims claims = extractAllClaims(token);

            return claims.getSubject().equals(userDetails.getUsername())
                    && "refresh".equals(claims.get("tokenType", String.class))
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
