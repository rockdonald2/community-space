package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserDTO;

import java.util.Set;

public interface StatusService {

    Set<UserDTO> getAllActive();

    void putActive(UserDTO userDTO);

    void putAllActive(Set<UserDTO> userDTOs);

    void removeActive(UserDTO userDTO);

}
