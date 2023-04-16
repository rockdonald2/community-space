package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.JwtService;
import edu.pdae.cs.accountmgmt.service.MessagingService;
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
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MessagingService messagingService;

    @PostMapping
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO userLoginDTO) throws LoginException { // NOSONAR
        log.info("Incoming login request for {}", userLoginDTO.getEmail());

        final var loginResponse = userService.login(userLoginDTO);
//        messagingService.sendMessageForActiveStatus(UserPresenceNotificationDTO.builder().email(userLoginDTO.getEmail()).status(UserPresenceNotificationDTO.Status.ONLINE).build());
        return loginResponse;
    }

    @GetMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> validate(@PathVariable("token") String token) {
        log.info("Incoming validate request before token validation");

        final String email = Objects.requireNonNull(jwtService.extractEmail(token)); // first check, check for e-mail and auth, we verify the signing key here
        // can throw validation exceptions

        log.info("Incoming validation request for {}", email);

        final User user = userRepository.findByEmail(email).orElseThrow(); // check whether we have a user according to the subject, should not occur

        // this can throw validation exceptions which means the claim was falsified
        if (jwtService.isTokenValid(token, user.getEmail())) { // check the expiration and subjects
//            messagingService.sendMessageForActiveStatus(UserPresenceNotificationDTO.builder().email(user.getEmail()).status(UserPresenceNotificationDTO.Status.ONLINE).build()); // TODO: rework this unnecessary side-effect
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/{token}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> logout(@PathVariable("token") String token) {
        log.info("Incoming logout request before token validation");

        final String email = Objects.requireNonNull(jwtService.extractEmail(token)); // can throw 400 or 401 if token is already expired or malicious, or email is missing

        log.info("Incoming logout request for {}", email);

//        messagingService.sendMessageForActiveStatus(UserPresenceNotificationDTO.builder().email(email).status(UserPresenceNotificationDTO.Status.OFFLINE).build());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Void> nullHandler() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Void> loginExceptionHandler() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Void> noElementHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExpiredJwtException.class, SignatureException.class})
    public ResponseEntity<Void> expiredOrBadSignatureHandler() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
