package edu.pdae.cs.accountmgmt.service;

import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String generateToken(String subject);

    String generateToken(Map<String, Object> extraClaims, String subject);

    boolean isTokenValid(String token, String potentialSubject);

    String extractEmail(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

}
