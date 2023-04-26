package edu.pdae.cs.accountmgmt.listener;

import edu.pdae.cs.accountmgmt.config.MessagingConfiguration;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.MessagingService;
import edu.pdae.cs.accountmgmt.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StatusListener {

    private final StatusService statusService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessagingService messagingService;

    @KafkaListener(topics = MessagingConfiguration.ACTIVE_STATUS_TOPIC,
            groupId = "cs-account-mgmt.active-status-group",
            autoStartup = "true",
            containerFactory = "presenceKafkaListenerContainerFactory")
    @Transactional
    public void statusListener(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught internal message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        messagingService.sendMessageForActiveStatusBroadcast();
    }

    @KafkaListener(topics = MessagingConfiguration.ACTIVE_STATUS_BROADCAST_TOPIC,
            groupId = "${random.uuid}", // random as this meant to be a broadcast topic, each consumer should get it
            autoStartup = "true",
            containerFactory = "broadcastKafkaListenerContainerFactory")
    @Transactional(readOnly = true)
    public void broadcastListener() {
        log.info("Caught internal message for presence broadcast");

        messagingTemplate.convertAndSend("/wb/status-broadcast", statusService.getAllActive());
    }

    @MessageMapping("/status-notify") // handles messages coming to /ws/status-notify
    @Transactional
    public void broadcastStatus(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught external (user) message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        messagingService.sendMessageForActiveStatusBroadcast();
    }

    @Scheduled(fixedDelayString = "${cs.status.broadcast.interval.minutes}", timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void broadcastStatus() {
        log.info("Broadcasting presence status");

        messagingService.sendMessageForActiveStatusBroadcast();
    }

    @Scheduled(fixedDelayString = "${cs.status.cleanup.interval.minutes}", timeUnit = TimeUnit.MINUTES)
    @Transactional
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
