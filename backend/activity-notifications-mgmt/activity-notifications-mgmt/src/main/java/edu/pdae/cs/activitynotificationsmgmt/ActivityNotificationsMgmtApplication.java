package edu.pdae.cs.activitynotificationsmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {RepositoryRestMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class ActivityNotificationsMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivityNotificationsMgmtApplication.class, args);
    }

}
