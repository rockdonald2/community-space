package edu.pdae.cs.gateway.service.impl;

import edu.pdae.cs.gateway.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${cs.auth.secret-key}")
    private String secretKey;

    @Override
    public boolean isTokenValid(String jwt) {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            return false;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
