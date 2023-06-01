package edu.pdae.cs.accountmgmt.listener;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pdae.cs.accountmgmt.model.dto.UserDataDTO;
import edu.pdae.cs.accountmgmt.model.dto.UserPresenceNotificationDTO;
import edu.pdae.cs.accountmgmt.service.StatusService;
import edu.pdae.cs.accountmgmt.service.impl.JwtServiceExtended;
import edu.pdae.cs.common.model.dto.UserPresenceDTO;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.net.HttpCookie;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
public class StatusListener {

    private final StatusService statusService;
    private final SocketIOServer socketIOServer;
    private final JwtServiceExtended jwtService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        socketIOServer.addConnectListener(onConnected());
        socketIOServer.addDisconnectListener(onDisconnected());
        socketIOServer.addEventListener(Events.NOTIFICATION.getValue(), UserPresenceNotificationDTO.class, onNotificationReceived());
    }

    private ConnectListener onConnected() throws JwtException {
        return client -> {
            final HandshakeData handshakeData = client.getHandshakeData();
            log.info("Client[{}] - Connected to status module '{}'", client.getSessionId().toString(), handshakeData.getUrl());

            final var userData = parseUserCookie(handshakeData.getHttpHeaders().get("Cookie"));
            if (userData.isEmpty()) {
                throw new JwtException("Invalid JWT token or cookies for WS connection");
            }

            client.joinRoom(userData.get().getEmail());

            log.info("Client[{}] - Approved connection - {}", client.getSessionId().toString(), userData.get().getEmail());
            statusService.putActive(UserPresenceDTO.builder().email(userData.get().getEmail()).build());
            broadcast();
        };
    }

    private DisconnectListener onDisconnected() throws JwtException {
        return client -> {
            log.info("Client[{}] - Disconnected", client.getSessionId().toString());

            final var userData = parseUserCookie(client.getHandshakeData().getHttpHeaders().get("Cookie"));
            if (userData.isEmpty()) {
                throw new JwtException("Invalid JWT token or cookies for WS connection");
            }

            client.leaveRoom(userData.get().getEmail());

            log.info("Client[{}] - Removing inactive status - {}", client.getSessionId().toString(), userData.get().getEmail());
            statusService.removeInactive(UserPresenceDTO.builder().email(userData.get().getEmail()).build());
            broadcast();
        };
    }

    private DataListener<UserPresenceNotificationDTO> onNotificationReceived() {
        return (client, data, ackSender) -> {
            log.debug("Client[{}] - Received status message '{}'", client.getSessionId().toString(), data);
            updateStatus(data);
            broadcast();
        };
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
        log.info("Broadcasting status");
        socketIOServer.getBroadcastOperations().sendEvent(Events.STATUS.getValue(), statusService.getAllActive(true));
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
        NOTIFICATION("notification"),
        STATUS("status");

        private final String value;
    }

}
