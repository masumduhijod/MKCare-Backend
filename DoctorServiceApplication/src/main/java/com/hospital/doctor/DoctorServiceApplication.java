package com.hospital.doctor;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DoctorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Doctor Service Started Successfully!");
        System.out.println("Service URL: http://localhost:8086");
        System.out.println("==============================================");
    }
}
