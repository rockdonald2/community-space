package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    private final KafkaTemplate<String, UserPresenceNotificationDTO> userPresenceDTOKafkaTemplate;

    @Override
    public void sendMessageForActiveStatus(UserPresenceNotificationDTO presenceDTO) {
        log.info("Sending active status message for {}", presenceDTO.getEmail());
        userPresenceDTOKafkaTemplate.send(MessagingConfiguration.ACTIVE_STATUS_TOPIC, presenceDTO);
    }

}
