package edu.pdae.cs.accountmgmt.service;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;

public interface MessagingService {

    void sendMessageForActiveStatus(UserPresenceNotificationDTO userDTO);

    void sendMessageForActiveStatusBroadcast();

}
