package ru.netology.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);

    @Value("${jwt.signingKey}")
    private String signingKey;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            logger.warn("JWT валидация не пройдена: {}", ex.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
    }
}