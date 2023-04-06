package edu.pdae.cs.accountmgmt.controller;

import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import edu.pdae.cs.accountmgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO userLoginDTO) throws LoginException { // NOSONAR
        return userService.login(userLoginDTO);
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginExceptionHandler() {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
