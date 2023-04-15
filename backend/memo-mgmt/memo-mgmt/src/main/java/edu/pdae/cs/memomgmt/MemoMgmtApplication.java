package edu.pdae.cs.memomgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;

@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
public class MemoMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoMgmtApplication.class, args);
    }

}
