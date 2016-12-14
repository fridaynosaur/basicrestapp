package com.pm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServer {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", EurekaServer.class.getSimpleName());
        SpringApplication.run(EurekaServer.class,  args);
    }
}
