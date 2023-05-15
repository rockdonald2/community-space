package edu.pdae.cs.accountmgmt.listener.interceptor;

import edu.pdae.cs.accountmgmt.model.dto.UserPresenceDTO;
import edu.pdae.cs.accountmgmt.service.JwtService;
import edu.pdae.cs.accountmgmt.service.StatusService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final StatusService statusService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Objects.requireNonNull(accessor);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("Validating JWT token for WS connection");
            final Optional<String> token = extractAuthorizationToken(message);

            if (token.isEmpty()) {
                log.warn("No JWT token for WS connection");
                throw new JwtException("No JWT token for WS connection");
            }

            final var isValid = jwtService.isTokenValid(token.get());
            final var email = jwtService.extractEmail(token.get()); // if not valid (falsified), will throw and end the flow with an error

            log.info("JWT token for WS connection is {} for {}", isValid ? "valid" : "invalid", email);

            if (!isValid) {
                throw new JwtException("Invalid JWT token for WS connection, possibly expired");
            }

            statusService.putActive(UserPresenceDTO.builder().email(email).build());
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            log.info("Closing WS connection");
            final Optional<String> token = extractAuthorizationToken(message);

            // sometimes it sends the header, other times it doesn't
            if (token.isPresent()) {
                final var email = jwtService.extractEmail(token.get()); // if not valid (falsified), will throw and end the flow with an error
                statusService.removeInactive(UserPresenceDTO.builder().email(email).build());
            }
        }

        return message;
    }

    private Optional<String> extractAuthorizationToken(Message<?> message) {
        final var headers = message.getHeaders();
        if (headers.containsKey("nativeHeaders")) {
            final var nativeHeaders = (java.util.Map<String, java.util.List<String>>) headers.get("nativeHeaders");
            final var token = nativeHeaders.get("Authorization").get(0).substring(7);
            return Optional.of(token);
        }

        return Optional.empty();
    }

}

