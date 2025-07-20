package com.aet.studyassistant.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("UserDetails and username cannot be null or empty");
        }
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // typically the email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails, UUID userId) {
        if (userDetails == null || userDetails.getUsername() == null || userDetails.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("UserDetails and username cannot be null or empty");
        }
        JwtBuilder builder = Jwts.builder()
                .setSubject(userDetails.getUsername()) // typically the email
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)); // 1 hour
        
        if (userId != null) {
            builder.claim("userId", userId.toString());
        }
        
        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
}