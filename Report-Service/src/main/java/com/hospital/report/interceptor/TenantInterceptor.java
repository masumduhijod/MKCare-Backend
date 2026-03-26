package com.hospital.report.interceptor;

import com.hospital.report.config.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Incoming request se X-Tenant-ID header extract karke TenantContext mein set karta hai.
 * Request complete hone ke baad thread-local clear karta hai (memory leak se bachao).
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String tenantId = request.getHeader("X-Tenant-ID");

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setTenantId(tenantId);
            System.out.println("🏥 [Report-Service] Tenant: " + tenantId
                    + " | URI: " + request.getRequestURI());
        } else {
            System.out.println("⚠️  [Report-Service] No X-Tenant-ID | URI: "
                    + request.getRequestURI());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        TenantContext.clear();
    }
}
