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
/**
 * Thread-local storage for current tenant context
 * Stores tenant ID for the current request thread
 */
public class TenantContext {
    
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    /**
     * Set tenant ID for current thread
     */
    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
        System.out.println("🏥 Tenant Context Set: " + tenantId);
    }
    
    /**
     * Get tenant ID from current thread
     */
    public static String getTenantId() {
        return currentTenant.get();
    }
    
    /**
     * Clear tenant context (call after request completes)
     */
    public static void clear() {
        currentTenant.remove();
    }
}
