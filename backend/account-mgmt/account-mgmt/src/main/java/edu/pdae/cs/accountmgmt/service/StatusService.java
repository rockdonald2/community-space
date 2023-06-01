package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.common.model.dto.UserPresenceDTO;

import java.util.Set;

public interface StatusService {

    Set<UserPresenceDTO> getAllActive(boolean force);

    void putActive(UserPresenceDTO userDTO);

    void removeInactive(UserPresenceDTO userDTO);

    void removeInactives(boolean force);

}
