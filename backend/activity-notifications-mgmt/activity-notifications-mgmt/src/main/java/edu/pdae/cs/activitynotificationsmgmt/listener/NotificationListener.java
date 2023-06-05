package edu.pdae.cs.activitynotificationsmgmt.listener;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pdae.cs.activitynotificationsmgmt.config.MessagingConfiguration;
import edu.pdae.cs.activitynotificationsmgmt.model.Notification;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.DueMemoDTO;
import edu.pdae.cs.activitynotificationsmgmt.model.dto.NotificationMessageDTO;
import edu.pdae.cs.activitynotificationsmgmt.service.NotificationService;
import edu.pdae.cs.common.model.dto.UserDataDTO;
import edu.pdae.cs.common.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;
    private final SocketIOServer socketIOServer;
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    @PostConstruct
    void init() {
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
    }

    @KafkaListener(
            topics = MessagingConfiguration.NOTIFICATIONS_TOPIC,
            groupId = "cs-activity-notifications-mgmt:notification-listener-group",
            autoStartup = "true",
            containerFactory = "notificationDTOConcurrentKafkaListenerContainerFactory"
    )
    public void notificationListener(@Payload NotificationMessageDTO notificationMessageDTO) {
        log.info("Caught internal message for notification: {}", notificationMessageDTO);
        notificationService.handleNotification(notificationMessageDTO);
    }

    @KafkaListener(
            topics = MessagingConfiguration.DUE_MEMO_TOPIC,
            groupId = "cs-activity-notifications-mgmt:due-memo-listener-group",
            autoStartup = "true",
            containerFactory = "dueMemoDTOConcurrentKafkaListenerContainerFactory"
    )
    public void dueMemoListener(@Payload DueMemoDTO dueMemoDTO) {
        log.info("Caught internal message for due memo: {}", dueMemoDTO);
        notificationService.handleDueMemo(dueMemoDTO);
    }

    private ConnectListener onConnected() {
        return client -> {
            final HandshakeData handshakeData = client.getHandshakeData();
            log.info("Client[{}] - Connected to status module '{}'", client.getSessionId().toString(), handshakeData.getUrl());

            final var userData = parseUserCookie(handshakeData.getHttpHeaders().get("Cookie"));
            if (userData.isEmpty()) {
                throw new JwtException("Invalid JWT token or cookies for WS connection");
            }

            client.joinRooms(Set.of(userData.get().getEmail(), Notification.GroupTargets.GENERAL.getValue()));

            log.info("Client[{}] - Approved connection - {}", client.getSessionId().toString(), userData.get().getEmail());
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client[{}] - Disconnected", client.getSessionId().toString());

            final var userData = parseUserCookie(client.getHandshakeData().getHttpHeaders().get("Cookie"));
            if (userData.isEmpty()) {
                throw new JwtException("Invalid JWT token or cookies for WS connection");
            }

            client.leaveRooms(Set.of(userData.get().getEmail(), Notification.GroupTargets.GENERAL.getValue()));
        };
    }

    private Optional<UserDataDTO> parseUserCookie(String cookie) throws JwtException {
        final HttpCookie encodedUserData = Arrays.stream(cookie.split(";"))
                .map(HttpCookie::parse)
                .filter(list -> list.get(0).getName().equals("user-data"))
                .findFirst()
                .orElseThrow(() -> new JwtException("Invalid JWT token for WS connection"))
                .stream().findFirst().orElseThrow(() -> new JwtException("Invalid JWT token for WS connection"));

        final String decodedUserData = URLDecoder.decode(encodedUserData.getValue(), StandardCharsets.UTF_8);
        UserDataDTO userDataDTO;
        try {
            userDataDTO = objectMapper.readValue(decodedUserData, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }

        final var isValid = jwtService.isTokenValid(userDataDTO.getToken());

        if (!isValid) {
            return Optional.empty();
        }

        return Optional.of(userDataDTO);
    }

    @Getter
    @AllArgsConstructor
    public enum Events {
        NOTIFICATION("notification");

        private final String value;
    }

}
