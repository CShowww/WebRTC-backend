package com.example.webrtcbackend.config;

import feign.Logger;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Value("${fhir.username}")
    private String username;

    @Value("${fhir.password}")
    private String password;

    @Bean
    public BasicAuthRequestInterceptor basicAuthInterceptor() {
        return new BasicAuthRequestInterceptor(username, password);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
