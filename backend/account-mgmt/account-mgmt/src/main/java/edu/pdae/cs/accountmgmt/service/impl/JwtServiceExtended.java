package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.common.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtServiceExtended extends JwtService {

    public JwtServiceExtended(@Value("${cs.auth.secret-key}") String secretKey) {
        super(secretKey);
    }

    public String generateToken(String subject) {
        return generateToken(new HashMap<>(), subject);
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        log.info("Generating new token for {}", subject);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
