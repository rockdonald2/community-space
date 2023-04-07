package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.model.Token;
import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationResponseDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.JwtService;
import edu.pdae.cs.accountmgmt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, LogoutHandler {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public UserCreationResponseDTO register(UserCreationDTO creationDTO) {
        final User reqUser = modelMapper.map(creationDTO, User.class);
        reqUser.setPassword(passwordEncoder.encode(reqUser.getPassword()));

        final Token jwtToken = Token.builder().data(jwtService.generateToken(reqUser.getEmail())).build();

        final User createdUser = userRepository.save(reqUser);

        return UserCreationResponseDTO
                .builder()
                .email(createdUser.getEmail())
                .id(createdUser.getId())
                .lastName(createdUser.getLastname())
                .firstName(createdUser.getFirstname())
                .token(jwtToken)
                .build();
    }

    @Override
    public UserLoginResponseDTO login(UserLoginDTO loginDTO) throws LoginException {
        final User repoUser = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow();
        if (!passwordEncoder.matches(loginDTO.getPassword(), repoUser.getPassword())) {
            throw new LoginException("Passwords don't match");
        }

        final Token jwtToken = Token.builder().data(jwtService.generateToken(loginDTO.getEmail())).build();

        return UserLoginResponseDTO
                .builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse resp, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // I will leave aside the logout here
        // we're not storing tokens in DB, we cannot invalidate them like this
        // logout basically means from client-side the token is cleared
        // however if the user keeps the same token, the logout had no effect
        // ? maybe we will use this for future notifications of logouts

        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }

}
