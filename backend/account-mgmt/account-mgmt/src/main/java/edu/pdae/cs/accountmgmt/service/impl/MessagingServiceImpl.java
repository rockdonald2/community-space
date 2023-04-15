package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final KafkaTemplate<String, UserPresenceNotificationDTO> userPresenceDTOKafkaTemplate;

    @Override
    public void sendMessageForActiveStatus(UserPresenceNotificationDTO presenceDTO) {
        userPresenceDTOKafkaTemplate.send(MessagingConfiguration.ACTIVE_STATUS_TOPIC, presenceDTO);
    }

}
