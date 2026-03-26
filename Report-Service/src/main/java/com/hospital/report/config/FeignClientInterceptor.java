package com.hospital.report.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign Request Interceptor
 * Sabhi outgoing Feign calls mein X-Tenant-ID header automatically add karta hai.
 *
 * Flow:
 *   Angular → API Gateway → Report Service (TenantInterceptor header set karta hai)
 *                                    ↓
 *             FeignClientInterceptor → Patient / CVR / Doctor / Billing / OPD Service
 */
@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String tenantId = TenantContext.getTenantId();

        if (tenantId != null && !tenantId.isEmpty()) {
            template.header("X-Tenant-ID", tenantId);
            System.out.println("🔗 [Report-Feign] X-Tenant-ID: " + tenantId
                    + " → " + template.method() + " " + template.url());
        } else {
            System.out.println("⚠️  [Report-Feign] WARNING: No tenant context for "
                    + template.method() + " " + template.url());
        }
    }
}
