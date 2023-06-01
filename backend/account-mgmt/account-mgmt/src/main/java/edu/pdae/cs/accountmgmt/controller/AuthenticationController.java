package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.UserService;
import edu.pdae.cs.accountmgmt.service.impl.JwtServiceExtended;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sessions")
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final JwtServiceExtended jwtService;
    private final UserRepository userRepository;

    @PostMapping
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO userLoginDTO) throws LoginException { // NOSONAR
        log.info("Incoming login request for {}", userLoginDTO.getEmail());
        return userService.login(userLoginDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> validate(@RequestHeader("Authorization") String rawHeader) {
        if (isAuthMissing(rawHeader)) {
            throw new IllegalArgumentException("Missing Authorization token");
        }

        log.info("Incoming validate request before token validation");

        final String token = getAuthToken(rawHeader);
        final String email = Objects.requireNonNull(jwtService.extractSubject(token)); // first check, check for e-mail and auth, we verify the signing key here
        // can throw validation exceptions, but will let through if it is expired

        log.info("Incoming validation request for {}", email);

        final User user = userRepository.findByEmail(email).orElseThrow(); // check whether we have a user according to the subject, should not occur

        // this can throw validation exceptions which means the claim was falsified
        if (jwtService.isTokenValid(token, user.getEmail())) { // check the expiration and subjects
            log.info("Incoming validation request for {} is valid", email);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        log.info("Incoming validation request for {} is invalid", email);
        // we know the token was valid here, but it is expired
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String rawHeader) {
        if (isAuthMissing(rawHeader)) {
            throw new IllegalArgumentException("Missing Authorization token");
        }

        log.info("Incoming logout request before token validation");

        final String token = getAuthToken(rawHeader);
        final String email = Objects.requireNonNull(jwtService.extractSubject(token)); // can throw 400 or 401 if token is malicious, or email is missing

        log.info("Incoming logout request for {}", email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getAuthToken(String rawHeaderEntry) {
        return rawHeaderEntry.split(" ")[1].trim();
    }

    private boolean isAuthMissing(String rawHeaderEntry) {
        return !rawHeaderEntry.startsWith("Bearer ");
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> nullHandler() {
        return ResponseEntity.badRequest().body("Invalid request received");
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginExceptionHandler() {
        return new ResponseEntity<>("Either wrong password or username", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noElementHandler() {
        return new ResponseEntity<>("Requested user cannot be found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class, IllegalArgumentException.class})
    public ResponseEntity<String> expiredOrBadSignatureHandler() {
        return new ResponseEntity<>("Invalid authentication token provided", HttpStatus.UNAUTHORIZED);
    }

}
