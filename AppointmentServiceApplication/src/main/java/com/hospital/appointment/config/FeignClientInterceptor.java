/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.appointment.config;

/**
 *
 * @author mduhijod
 */
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign Request Interceptor
 * Automatically adds X-Tenant-ID header to all Feign calls
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {
    
    @Override
    public void apply(RequestTemplate template) {
        String tenantId = TenantContext.getTenantId();
        
        if (tenantId != null && !tenantId.isEmpty()) {
            template.header("X-Tenant-ID", tenantId);
            System.out.println("🔗 [Feign] Adding X-Tenant-ID: " + tenantId + " to " + template.method() + " " + template.url());
        } else {
            System.out.println("⚠️ [Feign] WARNING: No tenant context for " + template.method() + " " + template.url());
        }
    }
}
