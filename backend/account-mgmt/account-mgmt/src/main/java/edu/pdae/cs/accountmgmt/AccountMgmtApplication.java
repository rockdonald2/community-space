package edu.pdae.cs.accountmgmt;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// if we don't exclude this it will give weird /profile and other endpoints
@SpringBootApplication(exclude = {RepositoryRestMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@RequiredArgsConstructor
public class AccountMgmtApplication {

    private final SocketIOServer socketIOServer;

    public static void main(String[] args) {
        SpringApplication.run(AccountMgmtApplication.class, args);
    }

    @PostConstruct
    void started() {
        socketIOServer.start();
    }

    @PreDestroy
    void stopped() {
        socketIOServer.stop();
    }

}
