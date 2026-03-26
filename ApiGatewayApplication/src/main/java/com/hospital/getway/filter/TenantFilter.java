/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hospital.getway.filter;

/**
 *
 * @author mduhijod
 */

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter to validate and propagate tenant ID
 * Excludes: auth, swagger, actuator, eureka endpoints
 */
@Component
public class TenantFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String tenantId = exchange.getRequest().getHeaders().getFirst("X-Tenant-ID");

        System.out.println("📍 [API Gateway] Request: " + path + " | Tenant: " + tenantId);

        if (shouldBypassTenantCheck(path)) {
            System.out.println("✅ [API Gateway] Bypassing tenant check for: " + path);
            return chain.filter(exchange);
        }

        if (tenantId == null || tenantId.isEmpty()) {
            System.out.println("❌ [API Gateway] Missing tenant ID for: " + path);
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        // ✅ Add/propagate tenant header to downstream request
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Tenant-ID", tenantId)
                        .build())
                .build();

        System.out.println("✅ [API Gateway] Forwarding tenant: " + tenantId + " to backend");
        return chain.filter(mutatedExchange);
    }

    private boolean shouldBypassTenantCheck(String path) {
        return path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/webjars/") ||
                path.contains("/swagger-resources") ||
                path.contains("/actuator/") ||
                path.startsWith("/api/auth/") || // Login, register, validate, clinic-info
                path.startsWith("/api/superadmin/") || // Super Admin APIs
                path.equals("/");
    }

    @Override
    public int getOrder() {
        return -1; // High priority - execute before other filters
    }
}