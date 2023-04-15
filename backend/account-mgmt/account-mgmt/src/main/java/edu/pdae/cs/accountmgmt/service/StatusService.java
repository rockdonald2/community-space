package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;

import java.util.Set;

public interface StatusService {

    Set<UserPresenceDTO> getAllActive();

    void putActive(UserPresenceDTO userDTO);

    void removeInactive(UserPresenceDTO userDTO);

    void removeInactives();

}
