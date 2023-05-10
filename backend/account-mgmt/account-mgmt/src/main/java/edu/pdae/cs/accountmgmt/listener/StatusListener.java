package edu.pdae.cs.accountmgmt.listener;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final String STATUS_BROADCAST = "/topic/status-broadcast";

    private final StatusService statusService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/status-notify") // handles messages coming to /ws/status-notify
    // @Transactional // olyan performance bottlenecket bevezet, hogy az valami elkepeszto
    public void broadcastStatus(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught external (user) message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        messagingTemplate.convertAndSend(STATUS_BROADCAST, statusService.getAllActive(true));
    }

    @Scheduled(fixedDelayString = "${cs.status.cleanup.interval.minutes}", timeUnit = TimeUnit.MINUTES)
    // @Transactional
    public void cleanupStatus() {
        log.info("Cleaning up presence status");

        statusService.removeInactives(false);
        messagingTemplate.convertAndSend(STATUS_BROADCAST, statusService.getAllActive(false));
    }

    private void updateStatus(UserPresenceNotificationDTO presenceNotificationDTO) {
        final var userPresenceDTO = UserPresenceDTO.builder().email(presenceNotificationDTO.getEmail()).build();

        switch (presenceNotificationDTO.getStatus()) { // NOSONAR
            case ONLINE -> statusService.putActive(userPresenceDTO);
            case OFFLINE -> statusService.removeInactive(userPresenceDTO);
        }
    }

}
