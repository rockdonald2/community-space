package edu.pdae.cs.accountmgmt.config;

import edu.pdae.cs.accountmgmt.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Value("${cs.cors.allowed-origins}")
    private String corsOrigins;
    @Value("${spring.rabbitmq.host}")
    private String relayHost;
    @Value("${spring.rabbitmq.relay.port}")
    private int relayPort;
    @Value("${spring.rabbitmq.username}")
    private String relayUser;
    @Value("${spring.rabbitmq.password}")
    private String relayPwd;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/account").setAllowedOrigins(corsOrigins);
        registry.addEndpoint("/stomp/account").setAllowedOrigins(corsOrigins).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/exchange") // prefix for controller endpoints
                .enableStompBrokerRelay("/topic")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setClientLogin(relayUser)
                .setClientPasscode(relayPwd)
                .setSystemLogin(relayUser)
                .setSystemPasscode(relayPwd)
                .setSystemHeartbeatReceiveInterval(10000)
                .setSystemHeartbeatSendInterval(10000)
                .setAutoStartup(true)
                .setTcpClient(createTcpClient());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("Validating JWT token for WS connection");
                    final var headers = message.getHeaders();
                    if (headers.containsKey("nativeHeaders")) {
                        final var nativeHeaders = (java.util.Map<String, java.util.List<String>>) headers.get("nativeHeaders");
                        final var token = nativeHeaders.get("Authorization").get(0).substring(7);
                        try {
                            final var email = jwtService.extractEmail(token); // if not valid, will throw and end the flow with an error
                            log.info("Valid JWT token for WS connection: {}", email);
                        } catch (JwtException e) {
                            log.warn("Invalid JWT token for WS connection");
                        }
                    }
                }
                return message;
            }
        });
    }

    private ReactorNettyTcpClient<byte[]> createTcpClient() {
        return new ReactorNettyTcpClient<>(relayHost, relayPort, new StompReactorNettyCodec());
    }

}
