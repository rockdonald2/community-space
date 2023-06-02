package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.Token;
import edu.pdae.cs.accountmgmt.model.User;
import edu.pdae.cs.accountmgmt.model.dto.*;
import edu.pdae.cs.accountmgmt.repository.UserRepository;
import edu.pdae.cs.accountmgmt.service.UserService;
import edu.pdae.cs.common.model.dto.UserMutationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtServiceExtended jwtService;
    private final KafkaTemplate<String, UserMutationDTO> userMutationDTOKafkaTemplate;

    @Override
    public UserCreationResponseDTO register(UserCreationDTO creationDTO) throws NoSuchElementException {
        log.info("Registering new user {}", creationDTO.getEmail());

        final User reqUser = modelMapper.map(creationDTO, User.class);
        reqUser.setPassword(passwordEncoder.encode(reqUser.getPassword()));

        final Token jwtToken = Token.builder().data(jwtService.generateToken(Map.of("FirstName", creationDTO.getFirstName(), "LastName", creationDTO.getLastName()), reqUser.getEmail())).build();
        final User createdUser = userRepository.save(reqUser);

        final var resp = UserCreationResponseDTO
                .builder()
                .email(createdUser.getEmail())
                .id(createdUser.getId().toString())
                .lastName(createdUser.getLastname())
                .firstName(createdUser.getFirstname())
                .token(jwtToken)
                .build();

        userMutationDTOKafkaTemplate.send(MessagingConfiguration.USERS_TOPIC, UserMutationDTO.builder()
                .id(createdUser.getId().toHexString())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstname())
                .lastName(createdUser.getLastname())
                .state(UserMutationDTO.State.ADDED)
                .build());

        return resp;
    }

    @Override
    public UserLoginResponseDTO login(UserLoginDTO loginDTO) throws LoginException {
        log.info("Logging in user {}", loginDTO.getEmail());

        final User repoUser = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new LoginException("User not found"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), repoUser.getPassword())) {
            throw new LoginException("Passwords don't match");
        }

        final Token jwtToken = Token.builder().data(jwtService.generateToken(Map.of("FirstName", repoUser.getFirstname(), "LastName", repoUser.getLastname()), loginDTO.getEmail())).build();

        return UserLoginResponseDTO
                .builder()
                .token(jwtToken)
                .email(repoUser.getEmail())
                .firstName(repoUser.getFirstname())
                .lastName(repoUser.getLastname())
                .build();
    }

    @Override
    public UserDetailsDTO findById(ObjectId id) throws NullPointerException {
        return modelMapper.map(userRepository.findById(id).orElseThrow(), UserDetailsDTO.class);
    }

    @Override
    public UserDTO findByEmail(String email) {
        return modelMapper.map(userRepository.findByEmail(email).orElseThrow(), UserDTO.class);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    @Override
    public void deleteById(ObjectId id) {
        userRepository.deleteById(id);

        userMutationDTOKafkaTemplate.send(MessagingConfiguration.USERS_TOPIC, UserMutationDTO.builder()
                .id(id.toHexString())
                .state(UserMutationDTO.State.REMOVED)
                .build());
    }

}
