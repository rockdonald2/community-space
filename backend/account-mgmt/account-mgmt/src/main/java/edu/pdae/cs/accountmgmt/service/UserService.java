package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserCreationDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserCreationResponseDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserLoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import javax.security.auth.login.LoginException;

public interface UserService {

    UserCreationResponseDTO register(UserCreationDTO creationDTO);

    UserLoginResponseDTO login(UserLoginDTO loginDTO) throws LoginException;

}
