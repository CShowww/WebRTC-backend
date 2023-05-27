package com.vd.backend;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableCaching
public class WebRtcBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebRtcBackendApplication.class, args);
    }

}
