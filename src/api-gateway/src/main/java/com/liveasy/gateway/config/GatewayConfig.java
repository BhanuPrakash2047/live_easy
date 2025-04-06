
package com.liveasy.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liveasy.gateway.filter.AuthenticationFilter;

@Configuration
public class GatewayConfig {
    
    @Autowired
    private AuthenticationFilter authFilter;
    
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                .route("load-service", r -> r.path("/api/load/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://load-service"))
                .route("booking-service", r -> r.path("/api/booking/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://booking-service"))
                .build();
    }
}
