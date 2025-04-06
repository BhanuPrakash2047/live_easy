
package com.liveasy.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    private Key key;
    
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    
    public String extractUserId(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }
    
    public String extractRole(String token) {
        return getAllClaimsFromToken(token).get("role", String.class);
    }
    
    private boolean isTokenExpired(String token) {
        return this.getAllClaimsFromToken(token).getExpiration().before(new Date());
    }
    
    public boolean isInvalid(String token) {
        try {
            return this.isTokenExpired(token);
        } catch (Exception e) {
            return true;
        }
    }
}
