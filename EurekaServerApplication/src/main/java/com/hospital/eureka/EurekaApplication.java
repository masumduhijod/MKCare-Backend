package com.hospital.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

	public static void main(String[] args) {
		  SpringApplication.run(EurekaApplication.class, args);
                  long heapSize = Runtime.getRuntime().totalMemory();
        long heapMax  = Runtime.getRuntime().maxMemory();
        long heapFree = Runtime.getRuntime().freeMemory();

        System.out.println("Total Heap: " + heapSize / (1024 * 1024) + " MB");
        System.out.println("Max Heap:   " + heapMax / (1024 * 1024) + " MB");
        System.out.println("Free Heap:  " + heapFree / (1024 * 1024) + " MB");
        System.out.println("==============================================");
        System.out.println("Eureka Server Started Successfully!");
        System.out.println("Access Dashboard: http://localhost:8761");
        System.out.println("==============================================");
	}

}
