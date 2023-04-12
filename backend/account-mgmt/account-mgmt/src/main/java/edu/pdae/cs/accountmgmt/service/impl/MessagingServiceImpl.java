package edu.pdae.cs.accountmgmt.service.impl;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final KafkaTemplate<String, UserPresenceDTO> userPresenceDTOKafkaTemplate;

    @Override
    public void sendMessageForActiveStatus(UserPresenceDTO presenceDTO) {
        userPresenceDTOKafkaTemplate.send(MessagingConfiguration.ACTIVE_STATUS_TOPIC, presenceDTO);
    }

}
