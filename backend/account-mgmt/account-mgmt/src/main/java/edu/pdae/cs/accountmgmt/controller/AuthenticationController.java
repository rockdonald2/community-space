package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.JwtService;
import edu.pdae.cs.accountmgmt.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO userLoginDTO) throws LoginException { // NOSONAR
        return userService.login(userLoginDTO);
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> verify(@PathVariable("token") String token) {
        final String email = jwtService.extractEmail(token); // first check, check for e-mail and auth

        if (email == null) {
            return ResponseEntity.badRequest().build();
        }

        final User user = userRepository.findByEmail(email).orElseThrow(); // check whether we have a user according to the subject

        // this can throw ValidationException which means the claim was falsified
        if (jwtService.isTokenValid(token, user.getEmail())) { // we verify with the signing key and check the expiration
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<?> loginExceptionHandler() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noElementHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> expiredSignatureHandler() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> badSignatureHandler() {
        return ResponseEntity.badRequest().build();
    }

}
