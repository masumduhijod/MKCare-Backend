/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.doctor.config;

/**
 *
 * @author mduhijod
 */
// ========== FeignClientConfig.java ==========


import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign Client Configuration
 * Registers interceptor for tenant header propagation
 */
@Configuration
public class FeignClientConfig {
    
    /**
     * Register Feign Request Interceptor
     */
    @Bean
    public RequestInterceptor feignTenantInterceptor() {
        return new FeignClientInterceptor();
    }
    
    /**
     * Enable full Feign logging for debugging
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
