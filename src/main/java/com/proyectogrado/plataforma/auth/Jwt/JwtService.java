package com.proyectogrado.plataforma.auth.Jwt;

import com.proyectogrado.plataforma.auth.Entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "dGVzdFNlY3JldEtleTEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";
    private static final long TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 1 hour expiration

    public String getToken(UserDetails user) {
        Map<String, Object> extraClaims = new HashMap<>();
        if (user instanceof User) {
            extraClaims.put("role", ((User) user).getRole().name()); // Agrega el rol al token
        }
        return getToken(extraClaims, user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        // Ensure getKey() returns a Key object (e.g., SecretKey)
        Key key = getKey();

        return Jwts
                .builder()
                .setClaims(extraClaims) // Add custom claims
                .setSubject(user.getUsername()) // Set subject (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set issued at time
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION)) // Set expiration time
                .signWith(key, SignatureAlgorithm.HS256) // Sign with the key and algorithm
                .compact(); // Compact into a JWT string
    }

    private Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // log o maneja como gustes
            throw e;
        }
    }


    public <T> T getClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return  claimsResolver.apply(claims);
    }

    private Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }
}
