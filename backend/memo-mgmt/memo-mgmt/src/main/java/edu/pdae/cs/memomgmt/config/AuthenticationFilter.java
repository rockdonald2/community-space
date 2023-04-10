package edu.pdae.cs.memomgmt.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pdae.cs.memomgmt.model.dto.UserValidationResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cs.account-mgmt.address}")
    private String accountMgmtAddress;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // only accept token based auth, reject otherwise
        }

        final String jwt = authHeader.substring(7); // extract bare JWT

        // make call to account-mgmt to validate token
        final Optional<UserValidationResponseDTO> validationResponse = checkTokenValidity(jwt);

        if (validationResponse.isPresent()) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        validationResponse.get().getEmail(), null, validationResponse.get().getAuthorities().stream().map(SimpleGrantedAuthority::new).toList()
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<UserValidationResponseDTO> checkTokenValidity(String jwt) {
        ResponseEntity<String> resp;
        try {
            resp = restTemplate.getForEntity(String.format("%s/api/v1/auth/%s", accountMgmtAddress, jwt), String.class);
        } catch (RestClientException e) {
            log.error("Exception while querying user identity", e);
            return Optional.empty();
        }

        if (!resp.getStatusCode().is2xxSuccessful()) {
            log.error("Invalid token received, letting it through without auth");
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(resp.getBody(), new TypeReference<>() {
            }));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Received response is of wrong type");
        }
    }

}
