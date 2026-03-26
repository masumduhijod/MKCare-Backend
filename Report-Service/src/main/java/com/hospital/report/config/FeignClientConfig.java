package com.hospital.report.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign logging level configuration.
 * BASIC  = method, URL, status, time  (production ke liye)
 * FULL   = headers + body bhi         (debug ke liye)
 */
@Configuration
public class FeignClientConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
