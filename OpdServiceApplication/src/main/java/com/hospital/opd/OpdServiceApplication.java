package com.hospital.opd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class OpdServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpdServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("OPD Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8087");
        System.out.println("==============================================");
    }
}
