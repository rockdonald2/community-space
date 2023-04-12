package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;

public interface MessagingService {

    void sendMessageForActiveStatus(UserPresenceDTO userDTO);

}
