package com.hospital.getway.filter;
import javax.ws.rs.core.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mduhijod
 */
public class AuthenticationFilter {
    @Bean
    public GatewayFilter authenticationFilter() {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token != null) {
                // Forward the token to user-service
                exchange = exchange.mutate()
                        .request(r -> r.header(HttpHeaders.AUTHORIZATION, token))
                        .build();
            }
            return chain.filter(exchange);
        };
    }
}
