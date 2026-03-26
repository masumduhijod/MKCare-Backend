/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.opd.interceptor;

/**
 *
 * @author mduhijod
 */

import com.hospital.opd.config.TenantContext;
import com.hospital.opd.util.DatabaseNameResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Intercepts all HTTP requests to extract and set tenant context
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    @Autowired
    private DatabaseNameResolver databaseNameResolver;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID");
        
        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setTenantId(tenantId);
            
            // ⭐ Load database names for this tenant
            databaseNameResolver.loadTenantDatabases(tenantId);
            
            System.out.println("🏥 Request received for Tenant: " + tenantId + " | Path: " + request.getRequestURI());
        } else {
            System.out.println("⚠️  No Tenant ID in request | Path: " + request.getRequestURI());
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // Clear tenant context after request completes
        TenantContext.clear();
    }
}
