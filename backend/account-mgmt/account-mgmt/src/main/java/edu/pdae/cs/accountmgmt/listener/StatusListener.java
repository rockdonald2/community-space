package edu.pdae.cs.accountmgmt.listener;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatusListener {

    @KafkaListener(topics = MessagingConfiguration.ACTIVE_STATUS_TOPIC,
            groupId = "cs-account-mgmt.active-status-group",
            autoStartup = "true",
            containerFactory = "userPresenceDTOKafkaListenerContainerFactory")
    public void activeStatusListener(@Payload UserPresenceDTO presenceDTO) {
        log.info("{}", presenceDTO.toString());
    }

}
