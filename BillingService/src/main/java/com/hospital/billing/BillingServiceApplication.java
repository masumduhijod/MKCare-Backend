package com.hospital.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class BillingServiceApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("✅ JVM Timezone set to Asia/Kolkata: " + new java.util.Date());
    }
    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Billing Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8088");
        System.out.println("==============================================");
    }
}
