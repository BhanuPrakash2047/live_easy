
package com.liveasy.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.liveasy.gateway.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GatewayFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for specific paths
        if (this.isAuthMissing(request)) {
            logger.error("Authorization header is missing");
            return this.onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        
        final String token = this.getAuthHeader(request);
        
        if (jwtUtil.isInvalid(token)) {
            logger.error("Authorization token is invalid");
            return this.onError(exchange, HttpStatus.UNAUTHORIZED);
        }
        
        this.populateRequestWithHeaders(exchange, token);
        
        return chain.filter(exchange);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
    
    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0).substring(7);
    }
    
    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }
    
    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);
        
        exchange.getRequest().mutate()
            .header("userId", userId)
            .header("role", role)
            .build();
    }
}
