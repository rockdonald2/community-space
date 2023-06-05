package edu.pdae.cs.activitynotificationsmgmt;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {RepositoryRestMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@RequiredArgsConstructor
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ActivityNotificationsMgmtApplication {

    private final SocketIOServer socketIOServer;

    public static void main(String[] args) {
        SpringApplication.run(ActivityNotificationsMgmtApplication.class, args);
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
