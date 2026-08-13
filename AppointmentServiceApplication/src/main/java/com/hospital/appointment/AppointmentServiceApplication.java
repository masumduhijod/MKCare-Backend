package com.hospital.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AppointmentServiceApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("✅ JVM Timezone set to Asia/Kolkata: " + new java.util.Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(AppointmentServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Appointment Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8083");
        System.out.println("==============================================");
    }
}
