package com.manosgrigorakis.logisticsplatform.security.jwt;

import com.manosgrigorakis.logisticsplatform.auth.model.UserInfoDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    public String JWT_SECRET;

    @Value("${spring.jwt.expiration-ms}")
    private Long JWT_EXPIRATION_MS;

    public JwtService() {
    }

    public String generateToken(Long id, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, id);
    }

    private String createToken(Map<String, Object> claims, Long id) {
        Date tokenExpiration = new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(id.toString())
                .setIssuedAt(new Date())
                .setExpiration(tokenExpiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsTResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsTResolver.apply(claims);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserInfoDetails userDetails) {
        final String userId = extractUserId(token);

        return (userId.equals(userDetails.getUserId().toString()) && !isTokenExpired(token));
    }
}
