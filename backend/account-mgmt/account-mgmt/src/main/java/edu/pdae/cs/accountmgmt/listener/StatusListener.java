package edu.pdae.cs.accountmgmt.listener;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StatusListener {

    public static final String STATUS_BROADCAST = "/topic/status-broadcast";
    public static final String STATUS_EXCHANGE = "/status-notify";

    private final StatusService statusService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping(STATUS_EXCHANGE) // handles messages coming to /ws/status-notify
    public void handleStatus(@Payload UserPresenceNotificationDTO presenceNotificationDTO) {
        log.info("Caught external (user) message for presence update for {}", presenceNotificationDTO);

        updateStatus(presenceNotificationDTO);
        broadcast();
    }

    @Scheduled(fixedDelayString = "${cs.status.broadcast.interval.seconds}", timeUnit = TimeUnit.SECONDS)
    public void broadcastStatus() {
        log.info("Broadcasting presence status");

        broadcast();
    }

    @Scheduled(fixedDelayString = "${cs.status.cleanup.interval.minutes}", timeUnit = TimeUnit.MINUTES)
    public void cleanupStatus() {
        log.info("Cleaning up presence status");

        statusService.removeInactives(false);
        broadcast();
    }

    private void updateStatus(UserPresenceNotificationDTO presenceNotificationDTO) {
        final var userPresenceDTO = UserPresenceDTO.builder().email(presenceNotificationDTO.getEmail()).build();

        switch (presenceNotificationDTO.getStatus()) { // NOSONAR
            case ONLINE -> statusService.putActive(userPresenceDTO);
            case OFFLINE -> statusService.removeInactive(userPresenceDTO);
        }
    }

    private void broadcast() {
        messagingTemplate.convertAndSend(STATUS_BROADCAST, statusService.getAllActive(true));
    }

}
