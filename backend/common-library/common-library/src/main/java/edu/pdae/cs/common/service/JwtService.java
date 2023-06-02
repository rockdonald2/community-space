package edu.pdae.cs.common.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@AllArgsConstructor
@Slf4j
public class JwtService {

    private String secretKey;

    public boolean isTokenValid(String token, String potentialSubject) throws JwtException {
        log.info("Validating token for potential subject {}", potentialSubject);

        final String email = extractSubject(token);
        return (email.equals(potentialSubject)) && !isTokenExpired(token); // will only throw if the token was falsified
    }

    public boolean isTokenValid(String token) throws JwtException {
        log.info("Validating token");

        return !isTokenExpired(token); // will only throw if the token was falsified
    }

    protected boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractSubject(String token) throws JwtException {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws JwtException {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    protected Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    protected Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } // let any other JwtException flow through this block
    }

    protected Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
