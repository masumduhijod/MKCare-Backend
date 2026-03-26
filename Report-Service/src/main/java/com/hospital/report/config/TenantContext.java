package com.hospital.report.config;

/**
 * Thread-local storage for current tenant context.
 * Report Service mein DB nahi hai — sirf Feign calls ke liye tenant ID store karta hai.
 */
public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        currentTenant.set(tenantId);
        System.out.println("🏥 [Report-Service] Tenant Context Set: " + tenantId);
    }

    public static String getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
