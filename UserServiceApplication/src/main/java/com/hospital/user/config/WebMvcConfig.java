/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.user.config;

/**
 *
 * @author mduhijod
 */


import com.hospital.user.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private TenantInterceptor tenantInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                // ⭐ CRITICAL: Exclude auth endpoints - they set context manually
                .excludePathPatterns(
                    "/auth/login",
                    "/auth/register",
                    "/auth/validate"
                );
        
        System.out.println("✅ Tenant Interceptor registered (excludes /auth/* endpoints)");
    }
}
