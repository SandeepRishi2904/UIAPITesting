package com.testframework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Module to handle modern Java Date/Time (LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());
        // This makes sure dates are written in a standard format (e.g., "2025-09-30T19:28:21.651")
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. Module to handle Hibernate-specific types and lazy loading
        mapper.registerModule(new Hibernate6Module());

        return mapper;
    }
}