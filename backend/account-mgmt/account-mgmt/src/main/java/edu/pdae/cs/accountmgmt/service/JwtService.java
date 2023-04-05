package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String generateToken(UserLoginDTO loginDTO);

    String generateToken(Map<String, Object> extraClaims, UserLoginDTO loginDTO);

    boolean isTokenValid(String token, UserLoginDTO loginDTO);

    String extractEmail(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

}
