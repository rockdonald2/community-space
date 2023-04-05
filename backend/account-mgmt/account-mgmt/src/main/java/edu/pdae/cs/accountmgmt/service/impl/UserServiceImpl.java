package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.model.Token;
import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
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

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, LogoutHandler {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public UserLoginResponseDTO register(UserCreationDTO creationDTO) {
        final User reqUser = modelMapper.map(creationDTO, User.class);
        reqUser.setPassword(passwordEncoder.encode(reqUser.getPassword()));

        final String jwtToken = jwtService.generateToken(modelMapper.map(reqUser, UserLoginDTO.class));
        reqUser.setTokens(Collections.singletonList(
                Token.builder()
                        .data(jwtToken).expired(false).revoked(false)
                        .build()
        ));

        userRepository.save(reqUser);

        return UserLoginResponseDTO
                .builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public UserLoginResponseDTO login(UserLoginDTO loginDTO) {
        final User repoUser = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow();
        final String jwtToken = jwtService.generateToken(loginDTO);

        repoUser.setTokens(Collections.singletonList(
                Token.builder()
                        .data(jwtToken).expired(false).revoked(false)
                        .build()
        ));
        userRepository.save(repoUser);

        return UserLoginResponseDTO
                .builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse resp, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }

        final String jwt = authHeader.substring(7);
        final String email = jwtService.extractEmail(jwt);
        final User user = userRepository.findByEmail(email).orElseThrow();

        user.setTokens(Collections.emptyList());
        userRepository.save(user);
        SecurityContextHolder.clearContext();
    }

}
