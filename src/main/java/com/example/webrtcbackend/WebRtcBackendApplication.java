package com.example.webrtcbackend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;


@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class WebRtcBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebRtcBackendApplication.class, args);
    }

}
