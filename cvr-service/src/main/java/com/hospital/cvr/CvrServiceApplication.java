package com.hospital.cvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class CvrServiceApplication {

    @PostConstruct
    public void init() {
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("✅ JVM Timezone set to Asia/Kolkata: " + new java.util.Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(CvrServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("CVR Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8085");
        System.out.println("==============================================");
    }
}
