package edu.pdae.cs.activitynotificationsmgmt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pdae.cs.common.service.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JwtService jwtService(@Value("${cs.auth.secret-key}") String secretKey) {
        return new JwtService(secretKey);
    }

}
