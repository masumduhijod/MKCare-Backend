package com.hospital.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableEurekaClient
public class PatientServiceApplication {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println("✅ JVM Timezone set to Asia/Kolkata: " + new java.util.Date());
    }

    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Patient Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8084");
        System.out.println("==============================================");
    }
}
