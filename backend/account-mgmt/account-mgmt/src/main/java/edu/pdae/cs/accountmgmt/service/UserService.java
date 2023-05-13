package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.*;
import org.bson.types.ObjectId;

import javax.security.auth.login.LoginException;
import java.util.List;

public interface UserService {

    UserCreationResponseDTO register(UserCreationDTO creationDTO);

    UserLoginResponseDTO login(UserLoginDTO loginDTO) throws LoginException;

    UserDetailsDTO findById(ObjectId id);

    UserDTO findByEmail(String email);

    List<UserDTO> findAll();

    void deleteById(ObjectId id);

}
