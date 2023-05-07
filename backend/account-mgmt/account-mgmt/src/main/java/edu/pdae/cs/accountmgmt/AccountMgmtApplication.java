package edu.pdae.cs.accountmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// if we don't exclude this it will give weird /profile and other endpoints
@SpringBootApplication(exclude = {RepositoryRestMvcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class AccountMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountMgmtApplication.class, args);
    }

}
