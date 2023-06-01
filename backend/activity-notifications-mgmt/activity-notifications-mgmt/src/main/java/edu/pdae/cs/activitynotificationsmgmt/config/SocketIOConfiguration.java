package edu.pdae.cs.activitynotificationsmgmt.config;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.corundumstudio.socketio.store.RedissonStoreFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@org.springframework.context.annotation.Configuration
public class SocketIOConfiguration {

    @Value("${cs.realtime.port}")
    private Integer port;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Bean
    public SocketIOServer socketIOServer() {
        final com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(port);
        config.setAckMode(AckMode.AUTO);
        config.setJsonSupport(new JacksonJsonSupport());
        config.setStoreFactory(redissonStoreFactory());
        config.setContext("/ws/notifications");
        config.setRandomSession(true);
        config.setWebsocketCompression(true);
        config.setTransports(Transport.WEBSOCKET);
        config.setMaxFramePayloadLength(1024 * 1024);
        return new SocketIOServer(config); // creates the config but doesn't start it yet
    }

    private RedissonStoreFactory redissonStoreFactory() {
        log.info("Connecting to Redis host: {}, over port: {}", redisHost, redisPort);
        final Config redissonConfig = new Config();
        redissonConfig.useSingleServer().setAddress(String.format("redis://%s:%s", redisHost, redisPort));
        final RedissonClient redisson = Redisson.create(redissonConfig);
        return new RedissonStoreFactory(redisson);
    }

}
