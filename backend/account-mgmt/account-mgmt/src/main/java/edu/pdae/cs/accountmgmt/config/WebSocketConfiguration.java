package edu.pdae.cs.accountmgmt.config;

import edu.pdae.cs.accountmgmt.listener.interceptor.WebSocketChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
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

    private final WebSocketChannelInterceptor webSocketInterceptor;

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
        registration.interceptors(webSocketInterceptor);
    }

    private ReactorNettyTcpClient<byte[]> createTcpClient() {
        return new ReactorNettyTcpClient<>(relayHost, relayPort, new StompReactorNettyCodec());
    }

}
