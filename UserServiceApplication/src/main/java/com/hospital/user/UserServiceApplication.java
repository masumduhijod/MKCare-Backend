package com.hospital.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("User Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8081");
        System.out.println("==============================================");
    }
}
