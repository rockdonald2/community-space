package edu.pdae.cs.accountmgmt.listener;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StatusListener {

    private final StatusService statusService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = MessagingConfiguration.ACTIVE_STATUS_TOPIC,
            groupId = "cs-account-mgmt.active-status-group",
            autoStartup = "true",
            containerFactory = "presenceKafkaListenerContainerFactory")
    public void innerListener(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught internal message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        broadcastStatus();
    }

    @MessageMapping("/status-notify") // handles messages coming to /ws/status-notify
    public void broadcastStatus(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught external (user) message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        broadcastStatus();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void broadcastStatus() {
        log.info("Broadcasting presence status");

        messagingTemplate.convertAndSend("/wb/status-broadcast", statusService.getAllActive());
    }

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES)
    public void cleanupStatus() {
        log.info("Cleaning up presence status");

        statusService.removeInactives();
    }

    private void updateStatus(UserPresenceNotificationDTO presenceNotificationDTO) {
        final var userPresenceDTO = UserPresenceDTO.builder().email(presenceNotificationDTO.getEmail()).build();

        switch (presenceNotificationDTO.getStatus()) { // NOSONAR
            case ONLINE -> statusService.putActive(userPresenceDTO);
            case OFFLINE -> statusService.removeInactive(userPresenceDTO);
        }
    }

}
